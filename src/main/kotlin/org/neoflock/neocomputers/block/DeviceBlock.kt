package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking

abstract class DeviceBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): BlockEntity(type, pos, state) {
    val connetionsInDir = MutableList(Direction.entries.size) { HashSet<DeviceNode>() }

    abstract fun getDeviceNodes(): List<DeviceNode>

    // Gets, if applicable, the node from a direction.
    // The direction is from this block entity to the original requester,
    // so it is Direction.UP if we asked from the one on the top side.
    abstract fun getNodeFromSide(directionToRequester: Direction): DeviceNode?

    open fun initNetworking(): DeviceBlockEntity {
        getDeviceNodes().forEach { Networking.addNode(it) }
        Direction.entries.forEach { handleConnectionsFor(it) }
        return this
    }

    open fun getCurrentlyConnectedNodesIn(direction: Direction): HashSet<DeviceNode> {
        val ent = level?.getBlockEntity(blockPos.relative(direction))
        val connected = HashSet<DeviceNode>()
        if(ent is DeviceBlockEntity) {
            val node = ent.getNodeFromSide(direction.opposite)
            if(node != null) connected.add(node)
        }
        return connected
    }

    // TODO: rethink this shi so sharing a node on 2 different sides doesn't make connections require mutually exclusive conditions
    // TODO: actually like, rethink the whole class so far

    open fun handleConnectionsFor(direction: Direction) {
        // refuse connections on no node to reduce CPU load
        val node = getNodeFromSide(direction.opposite) ?: return
        val old = connetionsInDir[direction.ordinal]
        val now = getCurrentlyConnectedNodesIn(direction)

        // TODO: optimize this hellscape

        val toKill = HashSet<DeviceNode>()
        old.forEach {
            if(it !in now) toKill.add(it)
        }
        toKill.forEach { node.disconnectFrom(it) }
        now.forEach {
            if(it !in old) node.connectTo(it)
        }
        connetionsInDir[direction.ordinal] = now
    }

    // TODO: optimize this sometime before our test computers melt
    open fun tickDevice() {
        // Handles device connections and sync here

        // Process connections
        Direction.entries.forEach {
            handleConnectionsFor(it)
        }
    }

    override fun setRemoved() {
        super.setRemoved()
        getDeviceNodes().forEach { Networking.removeNode(it) }
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
                blockEntity.tickDevice()
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