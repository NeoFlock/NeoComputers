package org.neoflock.neocomputers.gui.buffer;

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.utils.FontProvider
import org.neoflock.neocomputers.utils.GPUChar
import org.neoflock.neocomputers.utils.TextBuffer
import java.io.File
import kotlin.experimental.and
import kotlin.experimental.xor

class BufferRenderer(private var id: ResourceLocation, private var buffer: TextBuffer) { // TODO: NN buffer
    val CHARW = 8
    val CHARH = 16

    private var texwidth: Int = buffer.width*CHARW;
    private var texheight: Int = buffer.height*CHARH;
    private var image: NativeImage = NativeImage(texwidth, texheight, true); // idk what the boolean is
    private var tex: DynamicTexture = DynamicTexture(image)

    init {
        Minecraft.getInstance().textureManager.register(this.id, tex)
    }

    fun toRGBA(color: Int): Int {
        // Minecaft lies, its AGBR
        return java.lang.Integer.reverseBytes((color.toLong() * 256 + 0xFF).toInt())
    }

    fun drawGlyph(x: Int, y: Int, c: Char, fg: Int) {
        var glyph: ArrayList<Byte> = FontProvider.map[c]!!

        for (j in 0..<CHARH) {
            for (i in 0..<CHARW) {
                // var pixel = ((glyph[j] and ((1 shl (CHARW - i - 1)).toByte())).toInt()) ushr (CHARW - i - 1) // retardation
                var pixel = (glyph[j] and (0b10000000 ushr i).toByte()).toInt()
                if (pixel > 0) image.setPixelRGBA(x+i, y+j, toRGBA(fg))
            }
        }
    }

    fun drawBuffer() {
        for (i in 0..<buffer.width) {
            for (j in 0..<buffer.height) {
                var char: GPUChar = buffer.get(i, j)
                var x = i*CHARW
                var y = j*CHARH
                image.fillRect(x, y, CHARW, CHARH, toRGBA(char.bg))
                if (char.c != ' ' && char.c != '\u0000') drawGlyph(x, y, char.c, char.fg) 
            }
        }
        tex.upload()
    }

    fun clean() {
        Minecraft.getInstance().textureManager.release(this.id)
        image.close()
        tex.close()
    }
}