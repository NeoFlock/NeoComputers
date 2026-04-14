package org.neoflock.neocomputers.gui.screen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu

class CombustionGeneratorScreen(abstractContainerMenu: CombustionGeneratorMenu, inventory: Inventory, component: Component) : AbstractContainerScreen<CombustionGeneratorMenu>(abstractContainerMenu, inventory, component) {
    override fun init() {
        super.init()

        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2
    }

    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        val cx = (width - imageWidth) / 2
        val cy = (height - imageHeight) / 2

        val containerTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/linux.png")
        guiGraphics.blitSprite(containerTexture, cx, cy, imageWidth, imageHeight)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)
        super.renderTooltip(graphics, mouseX, mouseY)
    }
}