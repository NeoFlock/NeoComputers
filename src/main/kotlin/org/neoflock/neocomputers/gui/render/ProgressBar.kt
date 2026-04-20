package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier
import kotlin.math.ceil

// #66CC66

object ProgressBar { // TODO: variable length

    val BAR: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/bar.png")
//    val BAROLD: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/bar_old.png")
//    val font: Font = Minecraft.getInstance().font

    fun render(guiGraphics: GuiGraphics, x: Int, y: Int, value: Long, max: Long, mouseX: Int, mouseY: Int, width: Int=142, height: Int=14, tooltipfunc: (Int) -> String?) { // NOTE: OC never uses a different width and height, changing height is not recommended
//        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
//        RenderSystem.setShaderTexture(0, BAROLD)
//        drawQuad(x, y, width, height, 0F, 0F, 15F, 15F)
        renderEmptyBar(x, y, width, height)

        val frac = value.toFloat() / max.toFloat()
        val linew = ceil(frac*(width-2).toFloat())
        guiGraphics.fill(
            RenderType.guiOverlay(),
            x + 1,
            y + 1,
            x + 1 + (linew.toInt()),
            y + height-1,
            0xFF66CC66.toInt()
        )

        val tooltip = tooltipfunc((ceil(frac) * 100F).toInt()) ?: return
        if (mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height )
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(tooltip), mouseX, mouseY)
    }

    private fun renderEmptyBar(x: Int, y: Int, width: Int, height: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, BAR)
        drawQuad(x, y, 1, height, 0F, 0F, 1F, 14F, 3F, 14F)
        drawQuad(x+1, y, width-2, height, 1F, 0F, 2F, 14F, 3F, 14F)
        drawQuad(x+width-1, y, 1, height, 2F, 0F, 3F, 14F, 3F, 14F)

    }

    private fun drawQuad(x: Int, y: Int, width: Int, height: Int, u1: Float, v1: Float, u2: Float, v2: Float, texwidth: Float=15F, texheight: Float=15F) { // this should really become a util func
        var t: Tesselator = Tesselator.getInstance()
        var builder: BufferBuilder = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        builder.addVertex(x.toFloat(), (y+height).toFloat(), 1f).setUv(u1/texwidth, v2/texheight)
        builder.addVertex((x+width).toFloat(), (y+height).toFloat(), 1f).setUv(u2/texwidth, v2/texheight)
        builder.addVertex((x+width).toFloat(), y.toFloat(), 1f).setUv(u2/texwidth, v1/texheight)
        builder.addVertex(x.toFloat(), y.toFloat(), 1f).setUv(u1/texwidth,v1/texheight)

        BufferUploader.drawWithShader(builder.build()!!)
    }
}