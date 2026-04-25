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
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    NodeBlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    override val node = Networking.Node()
    var bound = "screen/unbound"

    val scrwidth: Short = 50
    val scrheight: Short = 16

    private var cleanrenderer: () -> Unit = { }; // TODO: THIS SUCKS, FIND A BETTER WAY

    override fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {
        super.encodeScreenData(player, packet)
        packet.writeShort(scrwidth.toInt())
        packet.writeShort(scrheight.toInt())
    }

    override fun tickNode(level: Level) {
        super.tickNode(level)
        if (bound == "screen/unbound") { // am i epstein or am i just retarded?
            createscreenstuffs()
        }
    }

    override fun setRemoved() {
        super.setRemoved()
        bound = "screen/unbound" // ensure no missing texture is displayed
        cleanrenderer()
    }

    private fun createscreenstuffs() {
        bound = "screen/"+node.address.toString().replace("-", "_")
        NeoComputers.LOGGER.info(bound)
        if (level!!.isClientSide) {
            var buffer: MutableList<BufferRenderer.GPUChar> = mutableListOf()
            for(char in node.address.toString()) {
                buffer.add(BufferRenderer.GPUChar(char))
            }
            for (i in 0..((scrwidth*scrheight)-36)) {
                buffer.add(BufferRenderer.GPUChar(' '))
            }

            var renderer: BufferRenderer = BufferRenderer(scrwidth.toInt(), scrheight.toInt(), ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, bound), buffer)
            renderer.drawBuffer()
            cleanrenderer = { renderer.clean() }
        }
    }
}