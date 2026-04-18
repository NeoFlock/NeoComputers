package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.gui.menu.ScreenMenu
import org.neoflock.neocomputers.gui.render.ScreenRenderer

class ScreenScreen : AbstractContainerScreen<ScreenMenu>{
    private var renderer: ScreenRenderer = ScreenRenderer();

    private var bufferRenderer: BufferRenderer? = null;
    constructor(abstractContainerMenu: ScreenMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component) {
        val buffer: ArrayList<BufferRenderer.GPUChar> = arrayListOf(BufferRenderer.GPUChar('h'), BufferRenderer.GPUChar('a'), BufferRenderer.GPUChar('i'))
        for (i in 0..<(400-3)) {
            buffer.add(BufferRenderer.GPUChar(' '))
        }
        bufferRenderer = BufferRenderer(20, 20, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/test"), buffer)
        bufferRenderer!!.register()
        bufferRenderer!!.drawBuffer()
        
        renderer.bind(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/test"))
    }
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {}
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)
        renderer.render(graphics, 50, 50, 100, 200)
    }

    override fun onClose() {
        super.onClose()
        bufferRenderer!!.clean()
    }
}