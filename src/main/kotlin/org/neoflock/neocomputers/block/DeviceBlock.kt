package org.neoflock.neocomputers.block

import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.NodeSynchronizer

abstract class SingleDeviceBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): DeviceBlockEntity(type, pos, state) {
    abstract val deviceNode: DeviceNode

    override fun getDeviceNodes() = listOf(deviceNode)
    override fun getNodeFromSide(directionToRequester: Direction): DeviceNode? = deviceNode
}

abstract class DeviceBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): BlockEntity(type, pos, state) {
    val connetionsInDir = MutableList<DeviceNode?>(Direction.entries.size) { null }
    var alreadySetup = false
    var receivedServerState = false
    var connectionsAreDirty = false

    abstract fun getDeviceNodes(): List<DeviceNode>

    // Gets, if applicable, the node from a direction.
    // The direction is from this block entity to the original requester,
    // so it is Direction.UP if we asked from the one on the top side.
    abstract fun getNodeFromSide(directionToRequester: Direction): DeviceNode?

    open fun processCommits(commits: Iterable<FriendlyByteBuf>) {
        receivedServerState = true
        val devs = getDeviceNodes()
        for (buf in commits) {
            val idx = buf.readVarInt()
            if(idx >= 0 && idx < devs.size) {
                devs[idx].processCommit(buf)
            }
        }
    }

    open fun initNetworking(): DeviceBlockEntity {
        if(hasLevel()) {
            alreadySetup = true
            Networking.addNodes(getDeviceNodes())
            Direction.entries.forEach { handleConnectionsFor(it) }
        }
        return this
    }

    // Cables are 1 node
    open fun getCurrentlyConnectedNodeIn(direction: Direction): DeviceNode? {
        val ent = level?.getBlockEntity(blockPos.relative(direction))
        if(ent is DeviceBlockEntity) {
            return ent.getNodeFromSide(direction.opposite)
        }
        return null
    }

    open fun handleConnectionsFor(direction: Direction) {
        // refuse connections on no node to reduce CPU load
        val node = getNodeFromSide(direction.opposite) ?: return
        val old = connetionsInDir[direction.ordinal]
        val now = getCurrentlyConnectedNodeIn(direction)

        if(old?.address != now?.address) {
            if(old != null) node.disconnectFrom(old)
            if(now != null) node.connectTo(now)
        }
        connetionsInDir[direction.ordinal] = now
    }

    // TODO: optimize this sometime before our test computers melt
    open fun tickDevice(level: Level) {
        // Handles device connections and sync here

        // we do it like this because stinky MC will call stuff before world is fully setup
        // and then not notify us of neighbour changes
        // this is because MC is considered shit
        if(!alreadySetup) {
            initNetworking()
        }
        if(connectionsAreDirty) {
            connectionsAreDirty = false
            Direction.entries.forEach { handleConnectionsFor(it) }
        }
    }

    open fun sendCommitsToClient(level: Level) {
        if(level !is ServerLevel) return
        // synchronization!
        val commits = mutableListOf<FriendlyByteBuf>()
        val devs = getDeviceNodes()
        for((i, dev) in devs.withIndex()) {
            if(dev.outOfSync) {
                dev.outOfSync = false
                val buf = FriendlyByteBuf(Unpooled.buffer())
                buf.writeVarInt(i)
                dev.writeFullStateCommit(buf)
                commits.addLast(buf)
            }
        }
        if(commits.isNotEmpty()) {
            level.players().forEach {
                val dist = it.position().distanceTo(blockPos.center)
                if(dist < 100) NetworkManager.sendToPlayer(it, NodeSynchronizer.DeviceBlockStatePayload(blockPos, commits))
            }
        }
    }

    open fun sendStateToPlayer(player: ServerPlayer) {
        val world = level!!
        if(world !is ServerLevel) return
        // synchronization!
        val commits = mutableListOf<FriendlyByteBuf>()
        val devs = getDeviceNodes()
        for((i, dev) in devs.withIndex()) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeVarInt(i)
            dev.writeFullStateCommit(buf)
            commits.addLast(buf)
        }
        if(commits.isNotEmpty()) {
            world.players().forEach {
                val dist = it.position().distanceTo(blockPos.center)
                if(dist <= NodeSynchronizer.MAX_STATE_DISTANCE_ALLOWED) NetworkManager.sendToPlayer(it, NodeSynchronizer.DeviceBlockStatePayload(blockPos, commits))
            }
        }
    }

    open fun requestServerState() {
        // no point
        if(receivedServerState) return
        // we're the server bro :sob:
        if(level?.isClientSide != true) return
        val player = Minecraft.getInstance().player ?: return
        // we assume the player will just reject, so we save on bandwidth
        if(player.position().distanceTo(blockPos.center) > NodeSynchronizer.MAX_STATE_DISTANCE_ALLOWED) return
        NetworkManager.sendToServer(NodeSynchronizer.DeviceBlockStateRequest(blockPos))
    }

    override fun setRemoved() {
        super.setRemoved()
        alreadySetup = false
        Networking.removeNodes(getDeviceNodes())
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        for (node in getDeviceNodes()) {
            node.markChanged()
        }
        receivedServerState = false
    }
}

abstract class DeviceBlock(properties: Properties = Properties.of()): BaseBlock(properties), EntityBlock {
    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T> {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T & Any) {
                if(blockEntity !is DeviceBlockEntity) return
                blockEntity.tickDevice(level)
                blockEntity.sendCommitsToClient(level)
                if(level.isClientSide) {
                    blockEntity.requestServerState()
                }
            }
        }
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        super.onPlace(state, level, pos, oldState, movedByPiston)
        val ent = level.getBlockEntity(pos)
        if(ent is DeviceBlockEntity) {
            ent.initNetworking()
        }
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
        val ent = level.getBlockEntity(pos)
        if(ent is DeviceBlockEntity) {
            ent.handleConnectionsFor(Direction.getNearest(neighborPos.center.subtract(pos.center)))
        }
    }
}