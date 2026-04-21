package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.gui.widget.ProgressBar
import org.neoflock.neocomputers.utils.GenericContainerScreen

class CaseScreen : GenericContainerScreen<CaseMenu> {
    private val PCB: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/computer.png")
    private val POWER_ATLAS: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button_power.png")
    private val BTN_SIZE = 18

    override fun shouldCenterTitle(): Boolean = false

    var isOn = false
    var energy = 0L
    var energyTotal = 0L
    var memUsed = 0L
    var memTotal = 0L
    var compUsed = 0L
    var compTotal = 0L

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        isOn = buf.readBoolean()
        energy = buf.readLong()
        energyTotal = buf.readLong()
        memUsed = buf.readLong()
        memTotal = buf.readLong()
        compUsed = buf.readLong()
        compTotal = buf.readLong()
    }
    
    constructor(abstractContainerMenu: CaseMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component)
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        super.renderBg(guiGraphics, f, i ,j)
        val relX = (this.width - this.imageWidth) / 2
        val relY = (this.height - this.imageHeight) / 2
        
        guiGraphics.blit(PCB, relX, relY, 0, 0, this.imageWidth, this.imageHeight)

        // this.renderSlots(relX, relY)
        
    }

    override fun renderCustomOverlay(graphics: GuiGraphics, mouseX: Int, mouseY: Int, blend: Float) {
        super.renderCustomOverlay(graphics, mouseX, mouseY, blend)
        val relX = (this.width - this.imageWidth) / 2
        val relY = (this.height - this.imageHeight) / 2

        graphics.blit(POWER_ATLAS, relX, relY, BTN_SIZE, BTN_SIZE, 0.5f, 0.5f, BTN_SIZE, BTN_SIZE, BTN_SIZE*2, BTN_SIZE*2)
    }

    // private fun renderSlots(relX: Int, relY: Int) { // TODO: put this in some generic screen class
    //     for (slot in menu.slots) {
    //         if (slot is DynamicSlot) {
    //             slot.draw(relX, relY)
    //         }
    //     }
    // }
}