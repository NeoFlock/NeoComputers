package org.neoflock.neocomputers.gui.screen;

import io.netty.buffer.Unpooled
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.tooltip.TooltipComponent
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.NodeSynchronizer
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.ButtonSprites
import org.neoflock.neocomputers.gui.widget.ImagerButton
import org.neoflock.neocomputers.utils.Formatting
import org.neoflock.neocomputers.utils.GenericContainerScreen
import java.util.Optional

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
    var lastError: String? = null
    var energy: Long = 0L
    var maxEnergy: Long = 0L
    var memory: Long = 0L
    var maxMemory: Long = 0L
    var components: Long = 0L
    var maxComponents: Long = 0L
    var arch = ""

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        super.processScreenStatePacket(buf)
        isOn = buf.readBoolean()
        btn?.pressed = isOn
        val error = buf.readByteArray().decodeToString()
        if(error.isEmpty()) {
            lastError = null
        } else {
            lastError = error
        }

        energy = buf.readLong()
        maxEnergy = buf.readLong()
        memory = buf.readLong()
        maxMemory = buf.readLong()
        components = buf.readLong()
        maxComponents = buf.readLong()
        arch = buf.readUtf()
    }

    fun computeButtonTooltip(): List<Component> {
        val msgs = mutableListOf(Component.literal("Computer " + if(isOn) "ON" else "OFF").withStyle(if(isOn) ChatFormatting.GREEN else ChatFormatting.RED))
        if(lastError != null) {
            msgs.addLast(Component.literal("Error: ").withStyle(ChatFormatting.RED).append(Component.literal(lastError!!)))
        }
        if(arch.isNotEmpty()) {
            msgs.addLast(Component.literal("Architecture: $arch"))
        }
        if(hasShiftDown()) {
            msgs.addLast(Component.literal("Energy: $energy / $maxEnergy J").withStyle(if(energy < 100) ChatFormatting.RED else ChatFormatting.WHITE))
            msgs.addLast(Component.literal("Memory: ${Formatting.formatMemory(memory)} / ${Formatting.formatMemory(maxMemory)}"))
            msgs.addLast(Component.literal("Components: $components / $maxComponents").withStyle(if(components <= maxComponents) ChatFormatting.WHITE else ChatFormatting.RED))
        }
        return msgs
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

    override fun renderCustomOverlay(graphics: GuiGraphics, mouseX: Int, mouseY: Int, blend: Float) {
        super.renderCustomOverlay(graphics, mouseX, mouseY, blend)
        if(btn!!.isHovered) {
            graphics.renderTooltip(this.font, computeButtonTooltip(), Optional.empty<TooltipComponent>(), mouseX, mouseY)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean { // todo: make a better widget system than mojang, practically not even using the fact it's a widget atp
        if (button == 0 && btn!!.isHovered) {
            btn!!.playDownSound(Minecraft.getInstance().soundManager)
            btn!!.onClick(mouseX, mouseY)
            return true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

}