package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import org.neoflock.neocomputers.NeoComputers

open class DynamicSlot(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {

    val BACKGROUND: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/slots/slot.png")
    val RENDER_TYPE = { r: ResourceLocation ->
        RenderType.create("nc_gui_slot", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder()
            .setShaderState(ShaderStateShard.POSITION_TEX_SHADER)
            .setTextureState(RenderStateShard.TextureStateShard(r, false, false))
            .setTransparencyState(RenderStateShard.TransparencyStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false))
    }

    open fun draw(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        drawQuad(graphics, BACKGROUND, x-1, y-1, 18, 18, 0F, 0F, 15F, 15F)
    }

    fun drawQuad(guiGraphics: GuiGraphics, tex: ResourceLocation, x: Int, y: Int, width: Int, height: Int, u1: Float, v1: Float, u2: Float, v2: Float) {
        val pose = guiGraphics.pose().last()
        val builder = guiGraphics.bufferSource().getBuffer(RENDER_TYPE(tex))

        builder.addVertex(pose, x.toFloat(), (y+height).toFloat(), 1f).setUv(u1/15F, v2/15F)
        builder.addVertex(pose, (x+width).toFloat(), (y+height).toFloat(), 1f).setUv(u2/15F, v2/15F)
        builder.addVertex(pose, (x+width).toFloat(), y.toFloat(), 1f).setUv(u2/15F, v1/15F)
        builder.addVertex(pose, x.toFloat(), y.toFloat(), 1f).setUv(u1/15F,v1/15F)
    }

}