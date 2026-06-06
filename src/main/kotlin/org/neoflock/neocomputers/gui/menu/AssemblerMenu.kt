package org.neoflock.neocomputers.gui.menu

import net.fabricmc.fabric.mixin.item.client.HeldItemRendererMixin
import net.minecraft.client.gui.MapRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.utils.GenericContainer
import org.neoflock.neocomputers.utils.GenericContainerMenu

class CaseSlot(container: Container, index: Int, x: Int, y: Int) : DynamicSlot(container, index, x, y) {
    val caseItems = listOf(Blocks.CASE_BLOCK.get().asItem())

    override fun mayPlace(stack: ItemStack): Boolean = stack.item in caseItems
}

class AssemblerMenu : GenericContainerMenu {

    constructor(id: Int, inv: Inventory) : this(id, inv, SimpleContainer(1))
    constructor(id: Int, inv: Inventory,  container: Container) : super(Menus.ASSEMBLER_MENU.get(), id, container) {
        this.addSlot(CaseSlot(container, 0, 12, 12))

        this.addInventoryHotbar(inv, 8, 84)
    }
}