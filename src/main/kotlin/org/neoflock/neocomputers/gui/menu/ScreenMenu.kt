package org.neoflock.neocomputers.gui.menu;

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.gui.menu.Menus;
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import org.neoflock.neocomputers.entity.ScreenEntity
import org.neoflock.neocomputers.utils.GenericContainerMenu

class ScreenMenu : GenericContainerMenu {
    var entity: ScreenEntity? = null

    constructor(i: Int, inv: Inventory, buf: FriendlyByteBuf) : this(i, inv, inv.player.level().getBlockEntity(buf.readBlockPos()))
    constructor(i: Int, inv: Inventory, entity: BlockEntity?) : super(Menus.SCREEN_MENU.get(), i, SimpleContainer(0)) {
        this.entity = entity as ScreenEntity
    }

}