package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
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

object ProgressBar {

    // NOTE: OC never uses a different width and height
    fun render(guiGraphics: GuiGraphics, x: Int, y: Int, value: Long, max: Long, mouseX: Int, mouseY: Int, width: Int=142, height: Int=14, tooltipfunc: (Int) -> String?) {
        RenderSystem.disableBlend()
        guiGraphics.fill(x, y, x+width-1, y+1, 0xFF373737.toInt()) // top left corner + top edge
        guiGraphics.fill(x, y+1, x+1, y+height-1, 0xFF373737.toInt()) // left edge

        guiGraphics.fill(x, y+height-1, x+1, y+height, 0xFF8B8B8B.toInt()) // bottom left corner
        guiGraphics.fill(x+width-1, y, x+width, y+1, 0xFF8B8B8B.toInt()) // top right corner

        guiGraphics.fill(x+1, y+height-1, x+width, y+height, 0xFFFFFFFF.toInt()) // bottom right corner + bottom edge
        guiGraphics.fill(x+width-1, y+height-1, x+width, y+1, 0xFFFFFFFF.toInt()) // right edge

        val frac = value.toFloat() / max.toFloat()
        val linew = ceil(frac*(width-2).toFloat())
        guiGraphics.fill(
            RenderType.gui(),
            x + 1,
            y + 1,
            x + 1 + (linew.toInt()),
            y + height-1,
            0xFF66CC66.toInt()
        )

        val tooltip = tooltipfunc((ceil(frac * 100F)).toInt()) ?: return
        if (mouseX > x && mouseX < x+width && mouseY > y && mouseY < y+height )
            guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.literal(tooltip), mouseX, mouseY)
    }

}