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
import net.minecraft.world.phys.Vec3
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.ButtonSprites
import org.neoflock.neocomputers.gui.widget.ImagerButton
import org.neoflock.neocomputers.sounds.Sounds
import org.neoflock.neocomputers.utils.Formatting
import org.neoflock.neocomputers.utils.GenericContainerScreen
import java.util.Optional

class CaseScreen : GenericContainerScreen<CaseMenu> {
    private val PCB: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/computer.png")
    private val BTN: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button_power.png")

    private var btn: ImagerButton? = null
    override fun shouldCenterTitle(): Boolean = false
//    override fun findMenuTexture(): ResourceLocation = BG

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

    fun getErrorComponent(err: String): Component = if(err.startsWith("@")) Component.translatable(err.substring(1)) else Component.literal(err)

    fun computeButtonTooltip(): List<Component> {
        val msgs = mutableListOf(Component.translatable(if(isOn) "neocomputers.computer.on" else "neocomputers.computer.off").withStyle(if(isOn) ChatFormatting.GREEN else ChatFormatting.RED))
        if(lastError != null) {
            msgs.addLast(Component.translatable("neocomputers.computer.errorNoMsg").withStyle(ChatFormatting.RED).append(getErrorComponent(lastError!!)))
        }
        if(arch.isNotEmpty()) {
            msgs.addLast(Component.translatable("neocomputers.arch", arch))
        }
        if(hasShiftDown()) {
            msgs.addLast(Component.translatable("neocomputers.computer.energy", energy, maxEnergy).withStyle(if(energy < maxEnergy/5) ChatFormatting.RED else ChatFormatting.WHITE))
            msgs.addLast(Component.translatable("neocomputers.computer.memory", Formatting.formatMemory(memory), Formatting.formatMemory(maxMemory)))
            msgs.addLast(Component.translatable("neocomputers.computer.components", components, maxComponents).withStyle(if(components <= maxComponents) ChatFormatting.WHITE else ChatFormatting.RED))
        }
        return msgs
    }

    constructor(abstractContainerMenu: CaseMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component) {
        btn = ImagerButton(
            71, 33,
            18, 18,
            ButtonSprites(BTN, 18, 18, 36, 36)
        ) {
            val buf = FriendlyByteBuf(Unpooled.buffer())
            buf.writeByte(if(isOn) 0x02 else 0x01)
            NodeSynchronizer.sendScreenInteraction(buf)
        }

        addWidget(btn!!)
    }
    override fun renderbg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        guiGraphics.blit(PCB, 0, 0, 0, 0, this.imageWidth, this.imageHeight) // WE'RE FREE
    }

    override fun renderCustomOverlay(graphics: GuiGraphics, mouseX: Int, mouseY: Int, blend: Float) {
        super.renderCustomOverlay(graphics, mouseX, mouseY, blend)
        if(btn!!.isHovered) {
            graphics.renderTooltip(this.font, computeButtonTooltip(), Optional.empty<TooltipComponent>(), mouseX, mouseY)
        }
    }

}