package org.neoflock.neocomputers.gui.render;

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.NativeImage
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers

class ScreenRenderer { 
    val BORDERS: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/borders.png")
    var bound: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound")

    val bordersize = 10

    fun render(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderTexture(0, BORDERS);
        // borders
        val bordersize = 8; // TODO: this should scale i think
        // top left corner, uv (0, 0) to (6,6)
        // graphics.blit
        drawQuad(graphics, x-bordersize, y-bordersize, bordersize, bordersize, 0F, 0F, 6F, 6F);
        // top border, uv (7,0) to (8,6)
        drawQuad(graphics, x, y-bordersize, width, bordersize, 7F, 0F, 8F, 6F); // x+bordersize-bordersize
        // top right corner, uv (9,0) to (15, 6)
        drawQuad(graphics, x+width, y-bordersize, bordersize, bordersize, 9F, 0F, 15F, 6F);
        
        // left border, uv (0,7) to (6, 8)
        drawQuad(graphics, x-bordersize, y, bordersize, height, 0F, 7F, 6F, 8F);
        // middle, uv (7, 7) to (8, 8)
        // drawQuad(graphics, x+bordersize, y+bordersize, width-bordersize, height-bordersize, 7, 7, 8, 8);
        // right border uv (9, 7) to (15, 8)
        drawQuad(graphics, x+width, y, bordersize, height, 9F, 7F, 15F, 8F);
        
        // bottom left corner, uv (0, 9) to (6, 15)
        drawQuad(graphics, x-bordersize, y+height, bordersize, bordersize, 0F, 9F, 6F, 15F);
        // bottom border, uv (7, 9) to (8, 15)
        drawQuad(graphics, x, y+height, width, bordersize, 7F, 9F, 8F, 15F);
        // bottom right corner, uv (9, 9) to (15, 15)
        drawQuad(graphics, x+width, y+height, bordersize, bordersize, 9F, 9F, 15F, 15F);

        RenderSystem.setShaderTexture(0, bound);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MIN_FILTER, GlConst.GL_NEAREST);
        RenderSystem.texParameter(GlConst.GL_TEXTURE_2D, GlConst.GL_TEXTURE_MAG_FILTER, GlConst.GL_NEAREST);
        
        drawQuad(graphics, x, y, width, height, 0F, 0F, 15F, 15F);

    }

    private fun drawQuad(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, u1: Float, v1: Float, u2: Float, v2: Float) {
        var t: Tesselator = Tesselator.getInstance()
        var builder: BufferBuilder = t.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)

        builder.addVertex(x.toFloat(), (y+height).toFloat(), 1f).setUv(u1/15F, v2/15F)
        builder.addVertex((x+width).toFloat(), (y+height).toFloat(), 1f).setUv(u2/15F, v2/15F)
        builder.addVertex((x+width).toFloat(), y.toFloat(), 1f).setUv(u2/15F, v1/15F)
        builder.addVertex(x.toFloat(), y.toFloat(), 1f).setUv(u1/15F,v1/15F)

        BufferUploader.drawWithShader(builder.build()!!)
    }

    fun bind(id: ResourceLocation) {
        bound = id
    }

    fun unbind() {
        bound = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound")
    }

    companion object Static {
        val img: NativeImage = NativeImage(1, 1, false)
        val tex: DynamicTexture = DynamicTexture(img)

        fun genUnboundTex() {
            img.fillRect(0, 0, 1, 1, 0xFF000000.toInt())
            tex.upload()

            Minecraft.getInstance().textureManager.register(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound"), tex)
        }

        fun cleanUnboundTex() {
            img.close()
            tex.close()
            Minecraft.getInstance().textureManager.release(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "screen/unbound"))
        }
    }
}