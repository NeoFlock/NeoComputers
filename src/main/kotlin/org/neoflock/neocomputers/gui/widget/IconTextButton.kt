package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ResourceLocationPattern
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier

class press(val lambda: (IconTextButton) -> Unit) : Button.OnPress {
    override fun onPress(button: Button) {
        lambda(button as IconTextButton)
    }
}

class narr : Button.CreateNarration {
    override fun createNarrationMessage(supplier: Supplier<MutableComponent?>): MutableComponent? {
        return supplier.get() // no narration for u
    }

}

class IconTextButton(x: Int, y: Int, text: String, val icon: ResourceLocation, val iconw: Int =16, val iconh: Int = 16, width: Int=150, height: Int=20, lambda: (IconTextButton) -> Unit) :
    Button(x, y, width, height, Component.literal(text),press(lambda), narr()) {


    var xOffset = 2
    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)

        RenderSystem.disableBlend() // i hate this
        val imx = x + xOffset
        val imy = y + (height/2) - (iconh/2)
        guiGraphics.blit(icon, imx, imy, 0f, 0f, iconw, iconh, iconw, iconh)
        RenderSystem.enableBlend()
    }

    override fun renderString(guiGraphics: GuiGraphics, font: Font, color: Int) {
        val startx = x + iconw/2 // idk why /2, might be coincidence thing
        renderScrollingString(guiGraphics, font, message, startx, y, startx+width, y+height, color)
    }

}