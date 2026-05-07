package org.neoflock.neocomputers.gui.screen

import io.netty.buffer.Unpooled
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.SpriteIconButton
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.RackEntity
import org.neoflock.neocomputers.gui.menu.RackMenu
import org.neoflock.neocomputers.gui.menu.RackSlot
import org.neoflock.neocomputers.gui.widget.IconTextButton
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.utils.GenericContainerScreen
import java.util.function.Supplier

class RackScreen(menu: RackMenu, inventory: Inventory, component: Component) : GenericContainerScreen<RackMenu>(menu, inventory, component) {
    override fun findMenuTexture(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/rack.png")
    val RELAY = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/relay.png")

    var relay_mode = if (menu.container is RackEntity) (menu.container as RackEntity).relayMode else false

    val relaybtn = IconTextButton(100, 96, if(relay_mode) "Enabled" else "Disabled", RELAY, width = 64) {
        if (relay_mode){
            it.message = Component.literal("Disabled")
            relay_mode = false
        } else {
            it.message = Component.literal("Enabled")
            relay_mode = true
        }

        val buffer = FriendlyByteBuf(Unpooled.buffer())
        buffer.writeBoolean(relay_mode)
        (menu.slots[0] as RackSlot).encode(buffer)
        NodeSynchronizer.sendScreenInteraction(buffer)
    }
    init {
        this.imageWidth = 175
        this.imageHeight = 209
        this.inventoryLabelY = imageHeight - 93
        addWidget(relaybtn)
    }

    override fun renderbg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        renderSideLabels(guiGraphics)
        if(relay_mode) renderRelayConnections(guiGraphics)
    }

    fun renderRelayConnections(graphics: GuiGraphics) {
        for(i in 0..3) {
            val x = 50+(i*11)
            graphics.fill(x, 104, x+4, 105, 0xffffffff.toInt())
            graphics.fill(x, 105, x+4, 106, 0xff888888.toInt())
        }
    }

    fun renderSideLabels(graphics: GuiGraphics) {
        val x = 115+7
        val y = 20

        graphics.drawString(font, "Bottom", x, y, 0x404040, false)
        graphics.drawString(font, "Top", x, y+11, 0x404040, false)
        graphics.drawString(font, "Back", x, y+22, 0x404040, false)
        graphics.drawString(font, "Right", x, y+33, 0x404040, false)
        graphics.drawString(font, "Left", x, y+44, 0x404040, false)
    }

//    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
//        super.processScreenStatePacket(buf)
////        NeoComputers.LOGGER.info("porcessing screen state packet...")
////        relay_mode = buf.readBoolean()
////        if (relay_mode) relaybtn.message = Component.literal("Enabled")
////        else relaybtn.message = Component.literal("Disabled")
////
////        for (slot in menu.slots) {
////            if (slot is RackSlot) {
////                slot.processStateScreenPacket(buf)
////            }
////        }
//
//    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (super.mouseClicked(mouseX, mouseY, button)) return true
        for (slot in menu.slots) {
            if (slot is RackSlot) {
                if(slot.mouseClicked(mouseX-imageX, mouseY-imageY, button)) {
                    val buf = FriendlyByteBuf(Unpooled.buffer())
                    buf.writeBoolean(relay_mode)
                    slot.encode(buf)

                    NodeSynchronizer.sendScreenInteraction(buf)
                    return true
                }
            }
        }

        return false;
    }
}