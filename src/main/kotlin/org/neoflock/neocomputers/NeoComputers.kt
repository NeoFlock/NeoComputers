package org.neoflock.neocomputers

import com.google.common.base.Suppliers
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.client.gui.MenuScreenRegistry
import dev.architectury.registry.menu.MenuRegistry
import dev.architectury.registry.registries.RegistrarManager
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.screen.ScreenScreen
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier


object NeoComputers {
    const val MODID: String = "neocomputers"
    val LOGGER: Logger = LoggerFactory.getLogger("NeoComputers")
    var PLATFORM: ModPlatform? = null


    fun entrypoint(platform: ModPlatform?) {
        PLATFORM = platform

        Blocks.BLOCKS.register();
        Blocks.registerBlockItems();
        Items.ITEMS.register();
        Tabs.TABS.register();
        Menus.MENUS.register();

        ClientLifecycleEvent.CLIENT_SETUP.register{
            MenuScreenRegistry.registerScreenFactory(Menus.SCREEN_MENU.get(), ::ScreenScreen) // TODO: this is different across different versions
        }

        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${NeoComputers.PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}