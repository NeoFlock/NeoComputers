package org.neoflock.neocomputers.gui.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu
import org.neoflock.neocomputers.utils.GenericContainerScreen

class CombustionGeneratorScreen(abstractContainerMenu: CombustionGeneratorMenu, inventory: Inventory, component: Component) : GenericContainerScreen<CombustionGeneratorMenu>(abstractContainerMenu, inventory, component) {
    override fun findMenuTexture(): ResourceLocation = ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png")

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)

        val lineBg = 0xFF002200.toInt()
        val lineFg = 0xFF00FF00.toInt()

        val lineX = imageX + 8
        val lineY = imageY + 6
        val lineHeight = 60

        val power = 0.2

        graphics.fill(lineX, lineY, lineX + 2, lineY + lineHeight, lineFg)
        graphics.fill(lineX, lineY, lineX + 2, lineY + (lineHeight * (1.0 - power)).toInt(), lineBg)
    }
}