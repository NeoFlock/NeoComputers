package org.neoflock.neocomputers

import com.google.common.base.Suppliers
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.registry.client.gui.MenuScreenRegistry
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.screen.ScreenScreen
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.network.Networking
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
        BlockEntities.BLOCKENTITIES.register()
        Menus.MENUS.register()
        Tabs.TABS.register();

        ClientLifecycleEvent.CLIENT_SETUP.register {
            MenuScreenRegistry.registerScreenFactory(Menus.SCREEN_MENU.get(), ::ScreenScreen)
        }

        TickEvent.SERVER_POST.register {
            Networking.tickAllNodes()
        }
        
        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${NeoComputers.PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}