package org.neoflock.neocomputers.gui.menu;

import dev.architectury.registry.menu.MenuRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.ScreenMenu
import org.neoflock.neocomputers.gui.screen.CaseScreen
import org.neoflock.neocomputers.gui.screen.CombustionGeneratorScreen
import org.neoflock.neocomputers.gui.screen.ScreenScreen

object Menus {
    val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(NeoComputers.MODID, Registries.MENU)

//    val SCREEN_MENU: RegistrySupplier<MenuType<ScreenMenu>> = MENUS.register("screen_menu") { MenuType(::ScreenMenu, FeatureFlagSet.of()) }
    val SCREEN_MENU: RegistrySupplier<MenuType<ScreenMenu>> = MENUS.register("screen_menu") { MenuRegistry.ofExtended<ScreenMenu>(::ScreenMenu) }
    val COMBUSTGEN_MENU: RegistrySupplier<MenuType<CombustionGeneratorMenu>> = MENUS.register("combustgen_menu") { MenuType(::CombustionGeneratorMenu, FeatureFlagSet.of() ) }
    val CASE_MENU: RegistrySupplier<MenuType<CaseMenu>> = MENUS.register("case_menu") { MenuType(::CaseMenu, FeatureFlagSet.of() )}

    fun registerScreens() {
        MenuScreens.register(Menus.COMBUSTGEN_MENU.get()) { m: CombustionGeneratorMenu, u, comp ->CombustionGeneratorScreen(m,u,comp)}
        MenuScreens.register(Menus.SCREEN_MENU.get(), ::ScreenScreen)
        MenuScreens.register(Menus.CASE_MENU.get(), ::CaseScreen)
    }
}