package org.neoflock.neocomputers.gui.menu;

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.gui.menu.Menus;
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.gui.widget.ComponentSlot
import org.neoflock.neocomputers.gui.widget.ComponentSlotRequirement
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.GenericContainerMenu

// lowest IQ machine to exist
class DumbMachine: MachineEntity {
    override fun getBlockPosition() = BlockPos.ZERO!!

    override fun isRunning() = false

    override fun start() = false

    override fun stop() = false

    override fun crash(error: String) = false

    override fun getMachineNode() = Networking.Node()

    override fun getRedstoneInput(direction: Direction): Int = 0

    override fun getRedstoneOutput(direction: Direction): Int = 0

    override fun setRedstoneOutput(direction: Direction, newValue: Int): Int = 0
}

class CaseMenu : GenericContainerMenu {
    constructor(i: Int, inv: Inventory) : this(i, inv, SimpleContainer(7))

    open val eepromRequirement = ComponentSlotRequirement(1, ComponentRoles.FIRMWARE)
    open val slotRequirements = listOf(
        listOf(ComponentSlotRequirement(1, ComponentRoles.CARD), ComponentSlotRequirement(1, ComponentRoles.CARD)),
        listOf(ComponentSlotRequirement(1, ComponentRoles.COMPUTE), ComponentSlotRequirement(1, ComponentRoles.MEMORY), ComponentSlotRequirement(1, ComponentRoles.MEMORY)),
        listOf(ComponentSlotRequirement(1, ComponentRoles.STORAGE)),
    )

    constructor(i: Int, inv: Inventory, container: Container) : super(Menus.CASE_MENU.get(), i, container) {
        this.addInventorySlots(inv, 8, 84)

        this.addSlot(ComponentSlot(this.container!!, 0, 20, 34, DumbMachine(), eepromRequirement))

        var i = 1
        for ((col, slotCol) in slotRequirements.withIndex()) {
            for ((row, slotReq) in slotCol.withIndex()) {
                this.addSlot(ComponentSlot(this.container!!, i, 98+(col*22), 18*(row+1)-2, DumbMachine(), slotReq))
                i++
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
}