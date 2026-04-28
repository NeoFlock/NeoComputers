package org.neoflock.neocomputers

import dev.architectury.event.events.client.ClientLifecycleEvent
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.architectury.platform.Platform
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.gui.menu.Menus
import dev.architectury.utils.Env
import dev.architectury.utils.EnvExecutor
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import org.neoflock.neocomputers.block.DeviceBlockEntity
import org.neoflock.neocomputers.gui.render.ScreenRenderer
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.sounds.Sounds
import org.neoflock.neocomputers.utils.FontProvider
import org.neoflock.neocomputers.utils.GenericContainerScreen
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NeoComputers {
    const val MODID: String = "neocomputers"
    val LOGGER: Logger = LoggerFactory.getLogger("NeoComputers")
    var PLATFORM: ModPlatform? = null

//    val BlockEntityRenderType: RenderType = RenderType.create(
//        "nc_blockentities",
//        DefaultVertexFormat.POSITION_TEX,
//        VertexFormat.Mode.QUADS,
//        0xc000, RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_TEX_SHADER).createCompositeState(false)) // TODO: figure out correct buffer size and composite state


    fun entrypoint(platform: ModPlatform?) {
        PLATFORM = platform

        Blocks.BLOCKS.register()
        Blocks.registerBlockItems()
        Items.ITEMS.register()
        BlockEntities.BLOCKENTITIES.register()
        BlockEntities.registerPowerBlocks()
        Menus.MENUS.register()
        Tabs.TABS.register()
        Sounds.SOUNDS.register()
        ComponentRoles.mapDefaultTextures()
        // I don't know why architectury wants two lambdas but whatever
        EnvExecutor.runInEnv(Env.CLIENT) {{
            ClientLifecycleEvent.CLIENT_SETUP.register {
                Menus.registerScreens()
            }
            ClientLifecycleEvent.CLIENT_STARTED.register {
                FontProvider.load(ResourceLocation.fromNamespaceAndPath(MODID, "font/unscii.hex"))
            }

            ClientLifecycleEvent.CLIENT_STOPPING.register {
                ScreenRenderer.cleanUnboundTex()
            }
        }}

        TickEvent.SERVER_POST.register {
            Networking.tickAllNodes()
            NodeSynchronizer.syncScreens()
        }

        TickEvent.PLAYER_POST.register {
            Sounds.tickCustomSounds()
        }

        LifecycleEvent.SERVER_STARTING.register {
            Networking.allNodes.remove()
            Networking.wirelessNodes.remove()
            Networking.channels.remove()
        }

        ClientLifecycleEvent.CLIENT_SETUP.register {
            Networking.allNodes.remove()
            Networking.wirelessNodes.remove()
            Networking.channels.remove()
        }

        PlayerEvent.CLOSE_MENU.register {
            player, menu ->
            if(player is ServerPlayer) NodeSynchronizer.playerScreenClosed(player)
        }

        PlayerEvent.PLAYER_QUIT.register {
            player ->
            NodeSynchronizer.playerScreenClosed(player)
        }

        // networking has no way to define a C2S packet type, so we need the listener on both
        // however, defining it separately on both breaks both ends
        // so we define it once, but on both platforms
        if(Platform.getEnvironment() == Env.CLIENT || Platform.getEnvironment() == Env.SERVER) {
            NetworkManager.registerReceiver(NetworkManager.c2s(),NodeSynchronizer.ScreenDataPayload.TYPE, NodeSynchronizer.ScreenDataPayload.CODEC, {
                    packet, ctx ->
                val player = ctx.player
                if(player is ServerPlayer) {
                    val ent = NodeSynchronizer.screenMap[player]
                    if(ent is DeviceNode) {
                        ent.processScreenInteraction(player, packet.buffer)
                    }
                }
            })

            NetworkManager.registerReceiver(NetworkManager.c2s(),NodeSynchronizer.DeviceBlockStateRequest.TYPE, NodeSynchronizer.DeviceBlockStateRequest.CODEC, {
                    packet, ctx ->
                val player = ctx.player
                val level = player.level()
                val dist = packet.blockPos.center.distanceTo(player.position())
                if(player is ServerPlayer && dist <= NodeSynchronizer.MAX_STATE_DISTANCE_ALLOWED) {
                    val ent = level.getBlockEntity(packet.blockPos)
                    if(ent is DeviceBlockEntity) {
                        ent.sendStateToPlayer(player)
                    }
                }
            })
        }

        // we have to do this because the datagen task runs in the physical server
        EnvExecutor.runInEnv(Env.CLIENT) {{
            NetworkManager.registerReceiver(NetworkManager.s2c(),NodeSynchronizer.DeviceBlockStatePayload.TYPE, NodeSynchronizer.DeviceBlockStatePayload.CODEC, {
                    packet, ctx ->
                val level = ctx.player.level()
                val ent = level.getBlockEntity(packet.blockPos)
                if(ent is DeviceBlockEntity) {
                    ent.processCommits(packet.buffers)
                }
            })

            NetworkManager.registerReceiver(NetworkManager.s2c(),NodeSynchronizer.ScreenPayload.TYPE, NodeSynchronizer.ScreenPayload.CODEC, {
                    packet, ctx ->
                val scr = Minecraft.getInstance().screen
                if(scr is GenericContainerScreen<*>) {
                    scr.processScreenStatePacket(packet.buffer)
                }
            })

            NetworkManager.registerReceiver(NetworkManager.s2c(),NodeSynchronizer.BeepDataPayload.TYPE, NodeSynchronizer.BeepDataPayload.CODEC, {
                    packet, ctx ->
                // TODO: implement volume
                Sounds.beep(packet.pos.center, packet.pattern, packet.freq, packet.duration.toMillis().toInt())
            })
        }}
        EnvExecutor.runInEnv(Env.SERVER) {{
            // https://github.com/architectury/architectury-api/issues/518
            NetworkManager.registerS2CPayloadType(NodeSynchronizer.DeviceBlockStatePayload.TYPE, NodeSynchronizer.DeviceBlockStatePayload.CODEC)
            NetworkManager.registerS2CPayloadType(NodeSynchronizer.ScreenPayload.TYPE, NodeSynchronizer.ScreenPayload.CODEC)
            NetworkManager.registerS2CPayloadType(NodeSynchronizer.BeepDataPayload.TYPE, NodeSynchronizer.BeepDataPayload.CODEC)
        }}

        LOGGER.info("Registered!")
        //LOGGER.info("Started mod in %s loader".formatted(NeoComputersInit.PLATFORM.getModloader()))
        //LOGGER.info("Kotlin: %s".formatted(NeoComputers.hello()))
        LOGGER.info("Started mod in ${PLATFORM?.modloader} loader")
        LOGGER.info("Hello from kotlin!")
    }
}