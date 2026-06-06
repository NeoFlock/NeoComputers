package org.neoflock.neocomputers.gui.screen

import io.netty.buffer.Unpooled
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.menu.AssemblerMenu
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.gui.widget.ButtonSprites
import org.neoflock.neocomputers.gui.widget.ImagerButton
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.utils.GenericContainerScreen

class AssemblerScreen : GenericContainerScreen<AssemblerMenu> {
    private var btn: ImagerButton? = null
    private val PCB: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/computer.png")
    private val BTN: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/button_power.png")

    constructor(abstractContainerMenu: AssemblerMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component) {
        btn = ImagerButton(
            8, 104,
            18, 18,
            ButtonSprites(BTN, 18, 18, 36, 36)
        ) {
//            val buf = FriendlyByteBuf(Unpooled.buffer())
//            buf.writeByte(if(isOn) 0x02 else 0x01)
//            NodeSynchronizer.sendScreenInteraction(buf)
        }

        addWidget(btn!!)
    }

    override fun renderbg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        guiGraphics.blit(PCB, 0, 0, 0, 0, this.imageWidth, this.imageHeight) // WE'RE FREE
    }
}