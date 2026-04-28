package org.neoflock.neocomputers.gui.screen

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.DyeColor
import org.neoflock.neocomputers.gui.menu.RelayMenu
import org.neoflock.neocomputers.utils.GenericContainerScreen
import kotlin.math.max
import kotlin.math.min

class RelayScreen(abstractContainerMenu: RelayMenu, inventory: Inventory, component: Component): GenericContainerScreen<RelayMenu>(abstractContainerMenu, inventory, component) {
    var interval: Int = 5
    var bufferSize: Int = 1
    var queueSize: Int = 20
    var inQueue: Int = 0

    override fun shouldCenterTitle(): Boolean = false

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        super.processScreenStatePacket(buf)
        interval = buf.readVarInt()
        bufferSize = buf.readVarInt()
        queueSize = buf.readVarInt()
        inQueue = buf.readVarInt()
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)
        val textX = imageX + 12
        val dataX = imageX + 100
        val textSpacing = 20
        val textY = imageY + 20
        val clr = ChatFormatting.DARK_GRAY.color!!

        graphics.drawString(this.font, "Cycle rate", textX, textY, clr, false)
        graphics.drawString(this.font, "Packets / cycle", textX, textY + textSpacing, clr, false)
        graphics.drawString(this.font, "Queue Size", textX, textY + textSpacing*2, clr, false)

        val hz = if(interval == 0) 20 else 20 / interval
        val buffered = min(inQueue, bufferSize)
        val queued = max(inQueue - bufferSize, 0)

        graphics.drawString(this.font, "$hz Hz", dataX, textY, clr, false)
        graphics.drawString(this.font, "$buffered / $bufferSize", dataX, textY + textSpacing, clr, false)
        graphics.drawString(this.font, "$queued / $queueSize", dataX, textY + textSpacing * 2, clr, false)
    }
}