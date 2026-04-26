package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.GPUChar
import org.neoflock.neocomputers.utils.TextBuffer

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    NodeBlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    override val deviceNode = object : DeviceNode() {
        override fun received(message: Networking.Message) {
            super.received(message)
            if(message is Networking.ComputerEvent) {
                // return if not directly connected
                if(message.sender !in this.connections) return
                val mEnv = message.machineEvent
                NeoComputers.LOGGER.info("Got message $mEnv!")
                if(mEnv is MachinePowerEvent) {
                    if(mEnv.nowRunning) {
                        textBuf.set(0, 0, address.toString())
                    } else {
                        textBuf.fill(0, 0, textBuf.width, textBuf.height, GPUChar(' '))
                    }
                }
            }
        }
    }
    var bound = "screen/unbound"

    val textBuf = TextBuffer(50, 16)

    private var cleanrenderer: () -> Unit = { }; // TODO: THIS SUCKS, FIND A BETTER WAY

    override fun encodeDownstreamData(packet: FriendlyByteBuf) {
        super.encodeDownstreamData(packet)
        textBuf.encodeContents(packet)
    }

    override fun syncWithUpstream(packet: FriendlyByteBuf) {
        super.syncWithUpstream(packet)
        textBuf.decodeContents(packet)
    }

    override fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {
        super.encodeScreenData(player, packet)
        textBuf.encodeContents(packet)
    }

    override fun tickNode(level: Level) {
        super.tickNode(level)
        cleanrenderer()
        createscreenstuffs()
    }

    override fun setRemoved() {
        super.setRemoved()
        bound = "screen/unbound" // ensure no missing texture is displayed
        cleanrenderer()
    }

    private fun createscreenstuffs() {
        bound = "screen/"+deviceNode.address.toString().replace("-", "_")
        if (level!!.isClientSide) {
            var renderer = BufferRenderer(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, bound), textBuf)
            renderer.drawBuffer()
            cleanrenderer = { renderer.clean() }
        }
    }
}