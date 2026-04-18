package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import org.neoflock.neocomputers.NeoComputers

open class DynamicSlot(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {

    val BACKGROUND: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/slots/slot.png")

    fun draw(graphics: GuiGraphics, relX: Int, relY: Int, mouseX: Int, mouseY: Int) {
        RenderSystem.enableBlend() // background
        RenderSystem.setShaderTexture(0, BACKGROUND)
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        drawQuad(relX+x-1, relY+y-1, 18, 18, 0F, 0F, 15F, 15F)
        RenderSystem.disableBlend()
    }

    private fun drawQuad(x: Int, y: Int, width: Int, height: Int, u1: Float, v1: Float, u2: Float, v2: Float) {
        var t: Tesselator = Tesselator.getInstance()
        var builder: BufferBuilder = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        builder.addVertex(x.toFloat(), (y+height).toFloat(), 1f).setUv(u1/15F, v2/15F)
        builder.addVertex((x+width).toFloat(), (y+height).toFloat(), 1f).setUv(u2/15F, v2/15F)
        builder.addVertex((x+width).toFloat(), y.toFloat(), 1f).setUv(u2/15F, v1/15F)
        builder.addVertex(x.toFloat(), y.toFloat(), 1f).setUv(u1/15F,v1/15F)

        BufferUploader.drawWithShader(builder.build()!!)
    }

    // private fun renderSlotHighlight(guiGraphics: GuiGraphics, x: Int, y: Int,  k: Int) { // im not sure but i tihnk i copied this from mc source code
    //     guiGraphics.fillGradient(RenderType.guiOverlay(), x, y, x + 16, y + 16, -2130706433, -2130706433, k);
    // }

}