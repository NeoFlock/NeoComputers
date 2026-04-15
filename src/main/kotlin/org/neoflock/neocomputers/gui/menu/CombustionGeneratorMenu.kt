package org.neoflock.neocomputers.gui.menu

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.utils.ContainerUtils
import org.neoflock.neocomputers.utils.GenericContainerMenu

class CombustionFuelSlot(container: Container, slot: Int, x: Int, y: Int): Slot(container, slot, x, y) {
    override fun mayPlace(itemStack: ItemStack): Boolean {
        return ContainerUtils.isBurningFuel(itemStack)
    }
}

class CombustionGeneratorMenu: GenericContainerMenu {
    // Client-side constructor, idk forge tells me to do this
    constructor(id: Int, inventory: Inventory): this(id, inventory, SimpleContainer(1))

    // Server-side constructor
    constructor(id: Int, inventory: Inventory, container: Container): super(Menus.COMBUSTGEN_MENU.get(), id, container) {
        container.startOpen(inventory.player)

        this.addSlot(CombustionFuelSlot(container, 0, 80, 35))

        this.addInventorySlots(inventory, 8, 84)
    }
}