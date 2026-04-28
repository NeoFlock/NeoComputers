package org.neoflock.neocomputers.gui.screen;

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.ScreenEntity
import org.neoflock.neocomputers.gui.buffer.BufferRenderer
import org.neoflock.neocomputers.gui.menu.ScreenMenu
import org.neoflock.neocomputers.gui.render.ScreenRenderer
import org.neoflock.neocomputers.utils.GenericContainerScreen
import org.neoflock.neocomputers.utils.TextBuffer
import kotlin.math.min

class ScreenScreen : GenericContainerScreen<ScreenMenu>{
    private var renderer: ScreenRenderer = ScreenRenderer();

    var textBuf = TextBuffer(0, 0)

    override fun shouldCenterTitle(): Boolean = false

    override fun processScreenStatePacket(buf: FriendlyByteBuf) {
        super.processScreenStatePacket(buf)
        textBuf.decodeContents(buf)
    }

    constructor(abstractContainerMenu: ScreenMenu, inventory: Inventory, component: Component) : super(abstractContainerMenu, inventory, component) {
        var ent: ScreenEntity = abstractContainerMenu.entity!!;
        renderer.bind(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, ent.bound))
        // advanced graphics programming
        this.titleLabelX = Int.MAX_VALUE
        this.inventoryLabelX = Int.MAX_VALUE
    }
    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        if(textBuf.width > 0) {
            imageWidth = textBuf.width * 4
            imageHeight = textBuf.height * 8
            renderer.render(guiGraphics, imageX, imageY, imageWidth, imageHeight)
        }
    }

//    override fun onClose() {
//        super.onClose()
//        renderer.
//    }
}