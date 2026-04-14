package org.neoflock.neocomputers

import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.TickEvent
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.screen.ScreenScreen
import dev.architectury.registry.menu.MenuRegistry
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.FontProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NeoComputers {
    const val MODID: String = "neocomputers"
    val LOGGER: Logger = LoggerFactory.getLogger("NeoComputers")
    var PLATFORM: ModPlatform? = null


    fun entrypoint(platform: ModPlatform?) {
        PLATFORM = platform

        Blocks.BLOCKS.register()
        Blocks.registerBlockItems()
        Items.ITEMS.register()
        BlockEntities.BLOCKENTITIES.register()
        BlockEntities.registerPowerBlocks()
        Menus.MENUS.register()
        Menus.registerScreens()
        Tabs.TABS.register()

        ClientLifecycleEvent.CLIENT_SETUP.register {
            MenuRegistry.registerScreenFactory(Menus.SCREEN_MENU.get(), ::ScreenScreen)
        }
        ClientLifecycleEvent.CLIENT_STARTED.register {
            FontProvider.load(ResourceLocation.fromNamespaceAndPath(MODID, "font/unscii.hex"))

            val buffer: ArrayList<BufferRenderer.GPUChar> = arrayListOf(BufferRenderer.GPUChar('h'), BufferRenderer.GPUChar('a'), BufferRenderer.GPUChar('i'))
            for (i in 0..<(400-3)) {
                buffer.add(BufferRenderer.GPUChar(' '))
            }
            val bufferRenderer = BufferRenderer(20, 20, ResourceLocation.fromNamespaceAndPath(MODID, "screen/test"), buffer)
            bufferRenderer.drawBuffer()
            // bufferRenderer.dump("/home/mewhenthe/code/NeoComputers/dump.png") // NOTE: CHANGE THIS BEFORE RUNNING!!!!
            bufferRenderer.clean()

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