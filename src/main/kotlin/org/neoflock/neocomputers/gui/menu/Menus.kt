package org.neoflock.neocomputers.gui.menu;

import dev.architectury.registry.menu.MenuRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.ScreenMenu

object Menus {
    val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(NeoComputers.MODID, Registries.MENU)

    val SCREEN_MENU: RegistrySupplier<MenuType<ScreenMenu>> = MENUS.register("screen_menu") { MenuRegistry.of(::ScreenMenu)} // "deprecated" my ass
}