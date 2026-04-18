package org.neoflock.neocomputers

import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.screen.ScreenScreen
import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.server.level.ServerPlayer
import org.neoflock.neocomputers.block.NodeBlock
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.block.NodeSynchronizer
import org.neoflock.neocomputers.gui.render.ScreenRenderer
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.FontProvider
import org.neoflock.neocomputers.utils.GenericContainerScreen
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
        Tabs.TABS.register()

        ClientLifecycleEvent.CLIENT_SETUP.register {
            MenuRegistry.registerScreenFactory(Menus.SCREEN_MENU.get(), ::ScreenScreen)
        }
        ClientLifecycleEvent.CLIENT_STARTED.register {
            FontProvider.load(ResourceLocation.fromNamespaceAndPath(MODID, "font/unscii.hex"))
            ScreenRenderer.genUnboundTex();
        }

        ClientLifecycleEvent.CLIENT_STOPPING.register {
            ScreenRenderer.cleanUnboundTex()
        }

        TickEvent.SERVER_POST.register {
            Networking.tickAllNodes()
            NodeSynchronizer.syncScreens()
        }

        PlayerEvent.CLOSE_MENU.register {
            player, menu ->
            if(player is ServerPlayer) NodeSynchronizer.playerScreenClosed(player)
        }

        PlayerEvent.PLAYER_QUIT.register {
            player ->
            NodeSynchronizer.playerScreenClosed(player)
        }

        NetworkManager.registerReceiver(NetworkManager.s2c(),NodeSynchronizer.StatePayload.TYPE, NodeSynchronizer.StatePayload.CODEC, {
            packet, ctx ->
            val level = ctx.player.level()
            val ent = level.getBlockEntity(packet.blockPos)
            if(ent is NodeBlockEntity) {
                ent.syncWithUpstream(packet.buffer)
            }
        })

        NetworkManager.registerReceiver(NetworkManager.s2c(),NodeSynchronizer.ScreenPayload.TYPE, NodeSynchronizer.ScreenPayload.CODEC, {
                packet, ctx ->
            val scr = Minecraft.getInstance().screen
            if(scr is GenericContainerScreen<*>) {
                scr.processScreenStatePacket(packet.buffer)
            }
        })
        
        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}