package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import io.netty.buffer.Unpooled
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.NodeSynchronizer
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.ButtonSprites
import org.neoflock.neocomputers.gui.widget.ImagerButton
import org.neoflock.neocomputers.utils.GenericContainerScreen

class CaseScreen : GenericContainerScreen<CaseMenu> {
    private val PCB: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/computer.png")
    private val BTN: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button_power.png")
//    private val BTN_ENABLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button/power/enabled.png") // gonna do this later
//    private val BTN_DISABLED: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button/power/disabled.png")
//    private val BTN_ENABLED_HOVER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button/power/enabled_hover.png")
//    private val BTN_DISABLED_HOVER: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/power/disabled_hover.png")

    private var btn: ImagerButton? = null
    override fun shouldCenterTitle(): Boolean = false

    var isOn = false

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        super.processScreenStatePacket(buf)
        isOn = buf.readBoolean()
        btn?.pressed = isOn
    }

    constructor(abstractContainerMenu: CaseMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component) {
        btn = ImagerButton(
            15, 15,
            18, 18,
            ButtonSprites(BTN, 18, 18, 36, 36)
        ) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeByte(if(isOn) 0x02 else 0x01)
            NodeSynchronizer.sendScreenInteraction(buf)
        }
//        addRenderableWidget(btn!!)
    }
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        super.renderBg(guiGraphics, f, i ,j)
        val relX = (this.width - this.imageWidth) / 2
        val relY = (this.height - this.imageHeight) / 2

        btn!!.x = relX+70
        btn!!.y = relY+33
        btn!!.render(guiGraphics, i, j, f) // minecraft SUCKSSS
        guiGraphics.blit(PCB, relX, relY, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean { // todo: make a better widget system than mojang, practically not even using the fact it's a widget atp
        NeoComputers.LOGGER.info(String.format("btn: %d %d %d %d, mouse %s %s", btn!!.x, btn!!.y, btn!!.x+btn!!.width, btn!!.y+btn!!.height, mouseX.toString(), mouseY.toString()))
        if (button != 0) return false
        if (btn!!.x < mouseX.toInt() && mouseX.toInt() < btn!!.x+btn!!.width && btn!!.y < mouseY.toInt() && mouseY.toInt() < btn!!.y+btn!!.height) {
            btn!!.playDownSound(Minecraft.getInstance().soundManager)
            btn!!.onClick(mouseX, mouseY)
            return true
        } else return false
    }

}