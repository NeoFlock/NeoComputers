package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.utils.GenericContainerScreen

class CaseScreen : GenericContainerScreen<CaseMenu> {
    private val PCB: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/computer.png")
    
    constructor(abstractContainerMenu: CaseMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component)
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        super.renderBg(guiGraphics, f, i ,j)
        val relX = (this.width - this.imageWidth) / 2
        val relY = (this.height - this.imageHeight) / 2
        
        guiGraphics.blit(PCB, relX, relY, 0, 0, this.imageWidth, this.imageHeight)

        // this.renderSlots(relX, relY)
        
    }

    // private fun renderSlots(relX: Int, relY: Int) { // TODO: put this in some generic screen class
    //     for (slot in menu.slots) {
    //         if (slot is DynamicSlot) {
    //             slot.draw(relX, relY)
    //         }
    //     }
    // }
}