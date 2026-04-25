package org.neoflock.neocomputers.block

import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.network.Networking
import java.time.Duration

object NodeSynchronizer {
    class StatePayload(var blockPos: BlockPos, var buffer: FriendlyByteBuf): CustomPacketPayload {
        companion object {
            val NODE_SYNC_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "node_sync")
            val TYPE = CustomPacketPayload.Type<StatePayload>(NODE_SYNC_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, StatePayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): StatePayload {
                    val blockPos = buf.readBlockPos()
                    val buffer = FriendlyByteBuf(buf.copy(buf.readerIndex(), buf.readableBytes()))
                    return StatePayload(blockPos, buffer)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: StatePayload) {
                    buf.writeBlockPos(payload.blockPos)
                    buf.writeBytes(payload.buffer)
                }
            }
        }

        override fun type() = TYPE
    }

    class ScreenPayload(var entityTypeWireID: String, var buffer: FriendlyByteBuf): CustomPacketPayload {
        companion object {
            val SCREEN_SYNC_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen_sync")
            val TYPE = CustomPacketPayload.Type<ScreenPayload>(SCREEN_SYNC_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ScreenPayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): ScreenPayload {
                    val id = buf.readByteArray().decodeToString()
                    val buffer = FriendlyByteBuf(buf.copy(buf.readerIndex(), buf.readableBytes()))
                    return ScreenPayload(id, buffer)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: ScreenPayload) {
                    buf.writeByteArray(payload.entityTypeWireID.encodeToByteArray())
                    buf.writeBytes(payload.buffer)
                }
            }
        }

        override fun type() = TYPE
    }

    class ScreenDataPayload(var entityTypeWireID: String, var buffer: FriendlyByteBuf): CustomPacketPayload {
        companion object {
            val SCREEN_DATA_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen_data")
            val TYPE = CustomPacketPayload.Type<ScreenDataPayload>(SCREEN_DATA_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ScreenDataPayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): ScreenDataPayload {
                    val id = buf.readByteArray().decodeToString()
                    val buffer = FriendlyByteBuf(buf.copy(buf.readerIndex(), buf.readableBytes()))
                    return ScreenDataPayload(id, buffer)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: ScreenDataPayload) {
                    buf.writeByteArray(payload.entityTypeWireID.encodeToByteArray())
                    buf.writeBytes(payload.buffer)
                }
            }
        }

        override fun type() = TYPE
    }

    class BeepDataPayload(val pos: BlockPos, val pattern: String, val freq: Int, val duration: Duration, val volume: Double): CustomPacketPayload {
        companion object {
            val BEEP_DATA_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "beep_data")
            val TYPE = CustomPacketPayload.Type<BeepDataPayload>(BEEP_DATA_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, BeepDataPayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): BeepDataPayload {
                    val pos = buf.readBlockPos()
                    val pattern = buf.readUtf()
                    val freq = buf.readVarInt()
                    val duration = buf.readVarLong()
                    val volume = buf.readDouble()
                    return BeepDataPayload(pos, pattern, freq, Duration.ofMillis(duration), volume)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: BeepDataPayload) {
                    buf.writeBlockPos(payload.pos)
                    buf.writeUtf(payload.pattern)
                    buf.writeVarInt(payload.freq)
                    buf.writeVarLong(payload.duration.toMillis())
                    buf.writeDouble(payload.volume)
                }
            }
        }

        override fun type() = TYPE
    }

    val screenMap = HashMap<ServerPlayer, NodeBlockEntity>()

    fun playerScreenClosed(player: ServerPlayer) {
        screenMap.remove(player)
    }

    fun nodeTypeToWireID(nodeType: BlockEntityType<*>): String = nodeType.javaClass.canonicalName

    fun registerPlayerScreen(player: ServerPlayer, entity: NodeBlockEntity) {
        screenMap[player] = entity
    }

    fun syncScreens() {
        for((player, ent) in screenMap) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            ent.encodeScreenData(player, buf)
            NetworkManager.sendToPlayer(player, ScreenPayload(nodeTypeToWireID(ent.type), buf))
        }
    }

    fun sendScreenInteraction(friendlyByteBuf: FriendlyByteBuf) {
        NetworkManager.sendToServer(ScreenDataPayload("", friendlyByteBuf))
    }

    fun emitBeep(level: Level, beepDataPayload: BeepDataPayload) {
        if(level is ServerLevel) {
            level.players().forEach {
                NetworkManager.sendToPlayer(it, beepDataPayload)
            }
        }
    }
}

abstract class NodeBlockEntity(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) : BlockEntity(blockEntityType, blockPos, blockState) {
    abstract val node: Networking.Node

    fun initNetworking(): NodeBlockEntity {
        Networking.addNode(node)
        invalidateNodeState()
        return this
    }

    // runs on the server, meant to encode state to send to all players
    open fun encodeDownstreamData(packet: FriendlyByteBuf) {
        packet.writeUUID(node.address)
        packet.writeLong(node.energy)
        packet.writeLong(node.energyCapacity)
        packet.writeEnum(node.reachability)
        packet.writeEnum(node.powerRole)
    }

    // runs on the client, meant to decode server state packets to synchronize client state
    open fun syncWithUpstream(packet: FriendlyByteBuf) {
        Networking.changeNodeAddress(node, packet.readUUID())
        node.energy = packet.readLong()
        node.energyCapacity = packet.readLong()
        node.reachability = packet.readEnum(node.reachability.javaClass)
        node.powerRole = packet.readEnum(node.powerRole.javaClass)
    }

    // Encodes data meant for the associated screen of a player
    open fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {}

    open fun processScreenInteraction(player: ServerPlayer, packet: FriendlyByteBuf) {}

    private var stateIsDirty = true

    open fun getNeighbourEntities(): List<BlockEntity> {
        val subpos = listOf(
            blockPos.offset(0, 0, 1),
            blockPos.offset(0, 0, -1),
            blockPos.offset(0, 1, 0),
            blockPos.offset(0, -1, 0),
            blockPos.offset(1, 0, 0),
            blockPos.offset(-1, 0, 0),
        )

        return subpos.mapNotNull { pos -> level?.getBlockEntity(pos) }
    }

    open fun computeEdges(): Set<NodeBlockEntity> {
        val s = mutableSetOf<NodeBlockEntity>()
        val neighbours = getNeighbourEntities()
        for(neighbour in neighbours) {
            if(neighbour is NodeBlockEntity) s.add(neighbour);
            // TODO: handle cable entities
        }
        s.remove(this)
        return s
    }

    open fun invalidateNodeState() {
        stateIsDirty = true
    }

    open fun needsSynchronization() = stateIsDirty

    open fun tickNode(level: Level) {
        if(!level.isClientSide) {
            val l = level as ServerLevel
            val packetBuf = FriendlyByteBuf(Unpooled.buffer())
            encodeDownstreamData(packetBuf)
            l.players().forEach {
                if(it.level().isLoaded(blockPos)) NetworkManager.sendToPlayer(it, NodeSynchronizer.StatePayload(blockPos, packetBuf))
            }
        }
        if(!stateIsDirty) return
        stateIsDirty = false
        computeEdges().forEach {
            node.connectTo(it.node)
        }
    }

    override fun setChanged() {
        invalidateNodeState()
        computeEdges().forEach { it.invalidateNodeState() }
        super.setChanged()
    }

    override fun setRemoved() {
        super.setRemoved()
        Networking.removeNode(node)
    }

    override fun clearRemoved() {
        super.clearRemoved()
        initNetworking()
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        invalidateNodeState()
        computeEdges().forEach { it.invalidateNodeState() }
    }
}

abstract class NodeBlock(properties: Properties = Properties.of()): BaseBlock(properties), EntityBlock {
    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is NodeBlockEntity) return
                if(Networking.getNode(blockEntity.node.address) == null) blockEntity.initNetworking()
                blockEntity.tickNode(level)
            }
        }
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        super.onPlace(blockState, level, blockPos, blockState2, bl)
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(blockPos)
            if(ent is NodeBlockEntity) {
                ent.invalidateNodeState()
                ent.computeEdges().forEach { it.invalidateNodeState() }
            }
            level.updateNeighborsAt(blockPos, this)
        }
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(blockPos)
            if(ent is NodeBlockEntity) {
                ent.invalidateNodeState()
            }

        }
    }
}