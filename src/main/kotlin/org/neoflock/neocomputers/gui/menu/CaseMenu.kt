package org.neoflock.neocomputers.gui.menu;

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.entity.CaseBlockEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.gui.widget.ComponentSlot
import org.neoflock.neocomputers.gui.widget.ComponentSlotRequirement
import org.neoflock.neocomputers.utils.GenericContainerMenu

open class CaseMenu : GenericContainerMenu {
    open val eepromRequirement = ComponentSlotRequirement(1, ComponentRoles.FIRMWARE)
    open val slotRequirements = listOf(
        listOf(ComponentSlotRequirement(1, ComponentRoles.CARD), ComponentSlotRequirement(1, ComponentRoles.CARD)),
        listOf(ComponentSlotRequirement(1, ComponentRoles.COMPUTE), ComponentSlotRequirement(1, ComponentRoles.MEMORY), ComponentSlotRequirement(1, ComponentRoles.MEMORY)),
        listOf(ComponentSlotRequirement(1, ComponentRoles.STORAGE)),
    )

    constructor(i: Int, inv: Inventory) : this(i, inv, SimpleContainer(7))

    constructor(i: Int, inv: Inventory, container: Container) : super(Menus.CASE_MENU.get(), i, container) {
        val machine = container as? CaseBlockEntity

        this.addSlot(ComponentSlot(container, 0, 20, 34, machine, eepromRequirement))

        var i = 1
        for ((col, slotCol) in slotRequirements.withIndex()) {
            for ((row, slotReq) in slotCol.withIndex()) {
                this.addSlot(ComponentSlot(container, i, 98+(col*22), 18*(row+1)-2, machine, slotReq))
                i++
            }
        }

        this.addInventorySlots(inv, 8, 84)

        // for (int col=1; col<4; col++) {
        //     for (int row=1; row<4; row++) {
        //         int i = (row-1)*3+(col-1);
        //         if(slotmap[tier][i] != null) {
        //             this.addSlot(new ComponentSlot(entity.getContainer(), ((col-1)*3)+row, 98+((col-1)*22), 18*row-2, slotmap[tier][i], tiermap[tier][i]));
        //         }
        //     }
        // }
    }
}