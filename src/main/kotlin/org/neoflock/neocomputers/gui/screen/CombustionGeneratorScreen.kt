package org.neoflock.neocomputers.gui.screen

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu
import org.neoflock.neocomputers.gui.widget.ProgressBar
import org.neoflock.neocomputers.utils.GenericContainerScreen
import kotlin.math.ceil

class CombustionGeneratorScreen : GenericContainerScreen<CombustionGeneratorMenu> {
//    val bar: ProgressBar = ProgressBar(x = -50, y = -50) { Pair(energy, energyCapacity) } // hide it type shi
    constructor(abstractContainerMenu: CombustionGeneratorMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component)

    var energy: Long = 0
    var energyCapacity: Long = 1

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)

        var relX: Int = (this.width - this.imageWidth) / 2;
        var relY: Int = (this.height - this.imageHeight) / 2;

        ProgressBar.render(graphics, relX+17, relY+55, energy, energyCapacity, mouseX, mouseY, 50, 14) {
            String.format("Energy: %d%% (%d/%d)", it, energy, energyCapacity)
        }
        // bar.x = imageX + 17
        // bar.y = imageY + 50

        // val lineBg = 0xFF002200.toInt()
        // val lineFg = 0xFF00FF00.toInt()

        // val lineX = imageX + 8
        // val lineY = imageY + 6
        // val lineHeight = 60

        // val power = energy.toDouble() / energyCapacity

        // graphics.fill(lineX, lineY, lineX + 2, lineY + lineHeight, lineFg)
        // graphics.fill(lineX, lineY, lineX + 2, lineY + (lineHeight * (1.0 - power)).toInt(), lineBg)
    }

    override fun getBoundBlockEntityType() = setOf(BlockEntities.COMBUSTGEN_ENTITY.get())

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        energy = buf.readLong()
        energyCapacity = buf.readLong()

    }
}