package org.neoflock.neocomputers

import com.google.common.base.Suppliers
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.registries.RegistrarManager
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.menu.Menus
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

        val logA = Networking.LoggerNode("LogA")
        val logB = Networking.LoggerNode("LogB")
        val batteryA = Networking.DebugBatteryNode(0.0, 10000.0)
        val batteryB = Networking.DebugBatteryNode(15000.0, 20000.0)
        logA.connectTo(logB)
        logA.connectTo(batteryA)
        logB.connectTo(batteryB)

        Networking.addNodes(logA, logB, batteryA, batteryB)

        Networking.emitMessage(logA, Networking.ClassicPacket(logA, "a", "b", 0, listOf(), 0))
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} ${batteryB.maxEnergyCapacity()}")
        Networking.tickAllNodes();
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} ${batteryB.maxEnergyCapacity()}")
        LOGGER.info("Had enough: ${if(logA.consumeEnergy(600.0)) 'Y' else 'N'}")
        LOGGER.info("A: ${batteryA.getEnergy()} / ${batteryA.maxEnergyCapacity()}, B: ${batteryB.getEnergy()} ${batteryB.maxEnergyCapacity()}")

        Networking.removeNodes(logA, logB, batteryA, batteryB)

        BlockEntities.BLOCKENTITIES.register()
        Menus.MENUS.register()
        
        Tabs.TABS.register();
        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${NeoComputers.PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}