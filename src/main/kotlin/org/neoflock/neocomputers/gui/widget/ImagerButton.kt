package org.neoflock.neocomputers.gui.widget

import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers

data class ButtonSprites(val sheet: ResourceLocation, val spriteWidth: Int, val spriteHeight: Int, val texWidth: Int, val texHeight: Int)

// minecraft sux
class ImagerButton(x: Int, y: Int, width: Int, height: Int, val sprites: ButtonSprites, onPress: Button.OnPress) : Button(x, y, width, height, Component.literal(""), onPress, DEFAULT_NARRATION) {
//    public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
//        ResourceLocation resourceLocation = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
//        guiGraphics.blitSprite(resourceLocation, this.getX(), this.getY(), this.width, this.height);
//    }
    var pressed = false

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        val u = if (pressed) 18F else 0F // no clue why it's swapped? prob cooked the coordinates, we gotta get parchment so bad
        val v = if (this.isHoveredOrFocused) 18F else 0F

        graphics.blit(sprites.sheet, x, y, width, height, u, v, sprites.spriteWidth, sprites.spriteHeight, sprites.texWidth, sprites.texHeight)
    }

}