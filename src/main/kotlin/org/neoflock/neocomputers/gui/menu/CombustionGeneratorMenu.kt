package org.neoflock.neocomputers.gui.menu

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class CombustionGeneratorMenu: AbstractContainerMenu {
    var container: Container

    // Client-side constructor, idk forge tells me to do this
    constructor(id: Int, inventory: Inventory): this(id, inventory, SimpleContainer(1))

    // Server-side constructor
    constructor(id: Int, inventory: Inventory, container: Container): super(Menus.COMBUSTGEN_MENU.get(), id) {
        this.container = container

        container.startOpen(inventory.player)

        this.addSlot(Slot(container, 0, 80, 35))

        // Based off the code in ChestMenu
        for (l in 0..2) {
            for (m in 0..8) {
                this.addSlot(Slot(inventory, m + l * 9 + 9, 8 + m * 18, 84 + l * 18))
            }
        }

        for (l in 0..8) {
            this.addSlot(Slot(inventory, l, 8 + l * 18, 84 + 3 * 18 + 4))
        }
    }

    // taken from https://docs.fabricmc.net/develop/blocks/container-menus
    override fun quickMoveStack(player: Player, i: Int): ItemStack? {
        val slot = slots[i]

        if(!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val copied = stack.copy()
        val contSize = container.containerSize

        if(i < contSize) {
            if(!this.moveItemStackTo(stack, contSize, slots.size, true)) {
                return ItemStack.EMPTY
            }
        } else if(!this.moveItemStackTo(stack, 0, contSize, false)) {
            return ItemStack.EMPTY
        }

        if(stack.isEmpty) {
            slot.setByPlayer(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }

        return copied
    }

    override fun stillValid(player: Player): Boolean {
        return container.stillValid(player)
    }
}