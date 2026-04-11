package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.gui.menu.ScreenMenu

class ScreenScreen(abstractContainerMenu: ScreenMenu, inventory: Inventory, component: Component) : AbstractContainerScreen<ScreenMenu>(abstractContainerMenu, inventory, component) {
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {}
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)
    }
}