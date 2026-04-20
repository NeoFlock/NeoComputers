package org.neoflock.neocomputers.gui.menu;

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.gui.menu.Menus;
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.utils.GenericContainerMenu

class CaseMenu : GenericContainerMenu {

    constructor(i: Int, inv: Inventory) : super(Menus.CASE_MENU.get(), i, SimpleContainer(10)) {
        this.addInventorySlots(inv, 8, 84)

        for (col in 0..2) {
            for (row in 0..2) {
                var i = col*3+row
                this.addSlot(DynamicSlot(this.container!!, i, 98+(col*22), 18*(row+1)-2))
            }
        }
        // for (int col=1; col<4; col++) {
        //     for (int row=1; row<4; row++) {
        //         int i = (row-1)*3+(col-1);
        //         if(slotmap[tier][i] != null) {
        //             this.addSlot(new ComponentSlot(entity.getContainer(), ((col-1)*3)+row, 98+((col-1)*22), 18*row-2, slotmap[tier][i], tiermap[tier][i]));
        //         }
        //     }
        // }
    }

    override fun stillValid(player: Player) = true // TODO: implement this properly
    override fun quickMoveStack(player: Player, i: Int): ItemStack = ItemStack.EMPTY // there's no container here anyways
}