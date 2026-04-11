package org.neoflock.neocomputers

import com.google.common.base.Suppliers
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.registry.registries.RegistrarManager
import org.neoflock.neocomputers.block.Blocks
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

        val logA = Networking.LoggerNode("LogA");
        val logB = Networking.LoggerNode("LogB");
        logA.connectTo(logB);
        // actually register them (else UB can happen)
        Networking.addNode(logA);
        Networking.addNode(logB);
        Networking.emitMessage(logA, Networking.ClassicPacket(logA, "shitfuck", "shitfuck2", 0, listOf(), 0));
        
        Tabs.TABS.register();
        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${NeoComputers.PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}