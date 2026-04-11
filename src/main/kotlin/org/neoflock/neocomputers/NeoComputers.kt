package org.neoflock.neocomputers

import com.google.common.base.Suppliers
import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.registry.client.gui.MenuScreenRegistry
import dev.architectury.registry.registries.RegistrarManager
import net.minecraft.resources.Identifier
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.screen.ScreenScreen
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.FontProvider
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
        ClientLifecycleEvent.CLIENT_STARTED.register {
            FontProvider.load(Identifier.fromNamespaceAndPath("neocomputers", "font/unscii.hex"))

            var buffer: ArrayList<BufferRenderer.GPUChar> = arrayListOf(BufferRenderer.GPUChar('h'), BufferRenderer.GPUChar('a'), BufferRenderer.GPUChar('i'))
            for (i in 0..<(400-3)) {
                buffer.add(BufferRenderer.GPUChar(' '))
            }
            var bufferRenderer = BufferRenderer(20, 20, Identifier.fromNamespaceAndPath(MODID, "screen/test"), buffer)
            bufferRenderer.drawBuffer()
            // bufferRenderer.dump("/home/mewhenthe/code/NeoComputers/dump.png") // NOTE: CHANGE THIS BEFORE RUNNING!!!!
            bufferRenderer.clean()

        }

        TickEvent.SERVER_POST.register {
            Networking.tickAllNodes()
        }

        val logA = Networking.LoggerNode("LogA")
        val logB = Networking.LoggerNode("LogB")
        val batteryA = Networking.DebugBatteryNode(0.0, 10000.0)
        val batteryB = Networking.DebugBatteryNode(15000.0, 20000.0)
        logA.connectTo(logB)
        logA.connectTo(batteryA)
        logB.connectTo(batteryB)

        Networking.addNodes(logA, logB, batteryA, batteryB)

        Networking.emitMessage(logA, Networking.ClassicPacket(logA, "a", "b", 0, listOf(), 0))
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} / ${batteryB.maxEnergyCapacity()}")
        Networking.tickAllNodes();
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} / ${batteryB.maxEnergyCapacity()}")
        LOGGER.info("Had enough: ${if(logA.consumeEnergy(600.0)) 'Y' else 'N'}")
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} / ${batteryB.maxEnergyCapacity()}")

        Networking.removeNodes(logA, logB, batteryA, batteryB)
        
        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${NeoComputers.PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}