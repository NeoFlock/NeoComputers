package org.neoflock.neocomputers.network

import dev.architectury.networking.NetworkManager
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import org.neoflock.neocomputers.NeoComputers
import java.time.Duration

object NodeSynchronizer {
    class DeviceBlockStatePayload(var blockPos: BlockPos, var buffers: List<FriendlyByteBuf>): CustomPacketPayload {
        companion object {
            val NODE_SYNC_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "node_sync")
            val TYPE = CustomPacketPayload.Type<DeviceBlockStatePayload>(NODE_SYNC_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, DeviceBlockStatePayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): DeviceBlockStatePayload {
                    val blockPos = buf.readBlockPos()
                    val bufferCount = buf.readVarInt()
                    val buffers = List(bufferCount) {
                        val bytes = buf.readByteArray()
                        val rawBuf = Unpooled.buffer(bytes.size)
                        rawBuf.writeBytes(bytes)
                        FriendlyByteBuf(rawBuf)
                    }
                    return DeviceBlockStatePayload(blockPos, buffers)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: DeviceBlockStatePayload) {
                    buf.writeBlockPos(payload.blockPos)
                    buf.writeVarInt(payload.buffers.size)
                    payload.buffers.forEach {
                        buf.writeByteArray(it.array())
                    }
                }
            }
        }

        override fun type() = TYPE
    }

    class ScreenPayload(var buffer: FriendlyByteBuf): CustomPacketPayload {
        companion object {
            val SCREEN_SYNC_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen_sync")
            val TYPE = CustomPacketPayload.Type<ScreenPayload>(SCREEN_SYNC_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ScreenPayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): ScreenPayload {
                    val buffer = FriendlyByteBuf(buf.copy(buf.readerIndex(), buf.readableBytes()))
                    return ScreenPayload(buffer)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: ScreenPayload) {
                    buf.writeBytes(payload.buffer)
                }
            }
        }

        override fun type() = TYPE
    }

    class ScreenDataPayload(var buffer: FriendlyByteBuf): CustomPacketPayload {
        companion object {
            val SCREEN_DATA_ID = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen_data")
            val TYPE = CustomPacketPayload.Type<ScreenDataPayload>(SCREEN_DATA_ID)
            val CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ScreenDataPayload> {
                override fun decode(buf: RegistryFriendlyByteBuf): ScreenDataPayload {
                    val buffer = FriendlyByteBuf(buf.copy(buf.readerIndex(), buf.readableBytes()))
                    return ScreenDataPayload(buffer)
                }

                override fun encode(buf: RegistryFriendlyByteBuf, payload: ScreenDataPayload) {
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

    val screenMap = HashMap<ServerPlayer, DeviceNode>()

    fun playerScreenClosed(player: ServerPlayer) {
        screenMap.remove(player)
    }

    fun registerPlayerScreen(player: ServerPlayer, devNode: DeviceNode) {
        screenMap[player] = devNode
    }

    fun nodeErased(node: DeviceNode) {
        var player: ServerPlayer? = null
        for((p, n) in screenMap) {
            if(n == node) player = p
        }
        if(player != null) screenMap.remove(player)
    }

    fun syncScreens() {
        for((player, ent) in screenMap) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            ent.encodeScreenData(player, buf)
            NetworkManager.sendToPlayer(player, ScreenPayload(buf))
        }
    }

    fun sendScreenInteraction(friendlyByteBuf: FriendlyByteBuf) {
        NetworkManager.sendToServer(ScreenDataPayload(friendlyByteBuf))
    }

    fun emitBeep(level: Level, beepDataPayload: BeepDataPayload) {
        if(level is ServerLevel) {
            level.players().forEach {
                NetworkManager.sendToPlayer(it, beepDataPayload)
            }
        }
    }
}