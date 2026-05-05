package org.neoflock.neocomputers.gui.menu

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.utils.GenericContainerMenu

class RackSlot(container: Container, slot: Int, x: Int, y: Int) : DynamicSlot(container, slot, x, y) {
    // i hate that i made this, my regret is immeasurable
    val MAIN_COLOURS = listOf(0xff8382d8.toInt(), 0xff75bdc1.toInt(), 0xffc8ca5f.toInt(), 0xffdb7d75.toInt(), 0xff7ec95f.toInt())
    val DARK_COLOURS = listOf(0xff6a6ab0.toInt(), 0xff60999d.toInt(), 0xffa2a44e.toInt(), 0xffb36660.toInt(), 0xff67a34e.toInt())
    val LIGHT_COLOURS = listOf(0xffdcdcf0.toInt(), 0xffdcdcf0.toInt(), 0xffececd4.toInt(), 0xfff0dbd9.toInt(), 0xffdbecd4.toInt())

    override fun draw(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        super.draw(graphics, mouseX, mouseY)
        if (!hasItem()) { drawQuad(graphics, ComponentRoles.getTextureFor("rack"), x, y, 16, 16, 0f, 0f, 15f, 15f); return; }
            // TODO: make this do stuff based on the item inputted
        drawConnection(graphics, item.count%5)
        drawConnection(graphics, (item.count+1)%5, 0)
        drawConnection(graphics, (item.count+2)%5, 1)

        drawEndpoints(graphics, 2)
    }

    fun drawConnection(guiGraphics: GuiGraphics, side: Int, sec: Int = -1) {
        val bufferSource = guiGraphics.bufferSource()
        val buffer = bufferSource.getBuffer(RenderType.gui())

        if (sec == -1) {
            drawColor(guiGraphics, buffer, 18, 1, DARK_COLOURS[side], 6 + (11 * side))
            drawColor(guiGraphics, buffer, 18, 2, MAIN_COLOURS[side], 6 + (11 * side))
            drawColor(guiGraphics, buffer, 18, 3, LIGHT_COLOURS[side], 6 + (11 * side))
        } else {
            drawColor(guiGraphics, buffer, 18, 6+(4*sec), MAIN_COLOURS[side], 6+(11*side))
            drawColor(guiGraphics, buffer, 18, 7+(4*sec), 0xff8f8f90.toInt(), 6+(11*side))
        }
    }


    fun drawEndpoints(guiGraphics: GuiGraphics, sec: Int =0) {
        val bufferSource = guiGraphics.bufferSource()
        val buffer = bufferSource.getBuffer(RenderType.gui())

        // main slot endpoint
        drawColor(guiGraphics, buffer, 17, 1, 0xff888888.toInt())
        drawColor(guiGraphics, buffer, 17, 3, 0xffffffff.toInt())

        // main cable endpoints
        for (i in 0..4) {
            drawColor(guiGraphics, buffer, 24+(11*i), 1, 0xff333333.toInt(), 1, 3)
            drawColor(guiGraphics, buffer, 25+(11*i), 1, MAIN_COLOURS[i], 3, 3)
            drawColor(guiGraphics, buffer, 28+(11*i), 1, 0xffffffff.toInt(), 1, 3)
        }

        // secondary endpoints
        for (i in 0..<sec) {
            // slot
            drawColor(guiGraphics, buffer, 17, 6+(4*i), 0xffffffff.toInt())
            drawColor(guiGraphics, buffer, 17, 7+(4*i), 0xff888888.toInt())

            // cable
            for (j in 0..4) {
                drawColor(guiGraphics, buffer, 24+(11*j), 6+(4*i), 0xff333333.toInt(), 1, 2)
                drawColor(guiGraphics, buffer, 25+(11*j), 6+(4*i), MAIN_COLOURS[j], 3, 2)
                drawColor(guiGraphics, buffer, 28+(11*j), 6+(4*i), 0xffffffff.toInt(), 1, 2)
            }
        }
    }

    // TODO: replace with graphics.fill (cant be assed atm)
    fun drawColor(guiGraphics: GuiGraphics, buffer: VertexConsumer, _x:Int, _y: Int, col: Int, width: Int=1, height: Int=1) {
        val pose = guiGraphics.pose().last()

        // x+_x+1 is one im not proud of
        buffer.addVertex(pose, x+_x+width.toFloat(), y+_y+height.toFloat(), 2f).setColor(col)
        buffer.addVertex(pose,  x+_x+width.toFloat(), y+_y.toFloat(), 2f).setColor(col)
        buffer.addVertex(pose, x+_x.toFloat(), y+_y.toFloat(), 2f).setColor(col)
        buffer.addVertex(pose, x+_x.toFloat(), y+_y+height.toFloat(), 2f).setColor(col)
    }
}

class RackMenu : GenericContainerMenu {

    constructor(i: Int, inv: Inventory) : this(i, inv, SimpleContainer(4))

    constructor(i: Int, inv: Inventory, container: Container) : super(Menus.RACK_MENU.get(), i, container) {
        for(i in 0..3) {
            this.addSlot(RackSlot(container,  i, 20, 23+i*20))
        }
        this.addInventorySlots(inv, 8, 128)
    }

}