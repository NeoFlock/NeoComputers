package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
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
    var render_on_block = false

    private var cleanrenderer: () -> Unit = { }; // TODO: THIS SUCKS, FIND A BETTER WAY

    override fun setChanged() {
        super.setChanged()
        if (bound == "screen/unbound") {
            createscreenstuffs()
        }
    }

    override fun setRemoved() {
        super.setRemoved()
        cleanrenderer()
    }

    private fun createscreenstuffs() {
        bound = "screen/"+node.address.toString().replace("-", "_")
        NeoComputers.LOGGER.info(bound)
        if (level!!.isClientSide) {
            var buffer: MutableList<BufferRenderer.GPUChar> = mutableListOf()
            for(char in node.address.toString()) {
                buffer.add(BufferRenderer.GPUChar(char, 0xFFFF00, 0x0000FF))
            }
            for (i in 0..((40*20)-36)) {
                buffer.add(BufferRenderer.GPUChar(' '))
            }

            var renderer: BufferRenderer = BufferRenderer(40, 20, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, bound), buffer)
            renderer.drawBuffer()
            cleanrenderer = { renderer.clean() }
        }
    }
}