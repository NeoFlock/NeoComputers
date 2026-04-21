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

object ProgressBar { // TODO: variable length

    val BAR: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/bar.png")

    // NOTE: OC never uses a different width and height, changing height is not recommended
    fun render(guiGraphics: GuiGraphics, x: Int, y: Int, value: Long, max: Long, mouseX: Int, mouseY: Int, width: Int=142, height: Int=14, tooltipfunc: (Int) -> String?) {
        guiGraphics.blit(BAR, x, y, 1, height, 0F, 0F, 1, 14, 3, 14)
        guiGraphics.blit(BAR, x+1, y, width-2, height, 1F, 0F, 1, 14, 3, 14)
        guiGraphics.blit(BAR, x+width-1, y, 1, height, 2F, 0F, 1, 14, 3, 14)

        val frac = if(max == 0L) 0.0f else value.toFloat() / max.toFloat()
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