package org.neoflock.neocomputers.gui.menu;

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.gui.menu.Menus;
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.utils.GenericContainerMenu

class ScreenMenu(i: Int, inv: Inventory) : GenericContainerMenu(Menus.SCREEN_MENU.get(), i, SimpleContainer(0)) {
}