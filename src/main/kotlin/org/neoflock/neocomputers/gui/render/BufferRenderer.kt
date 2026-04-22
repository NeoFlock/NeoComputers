package org.neoflock.neocomputers.gui.buffer;

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.utils.FontProvider
import java.io.File
import kotlin.experimental.and
import kotlin.experimental.xor

class BufferRenderer(private var width: Int, private var height: Int, private var id: ResourceLocation, private var buffer: MutableList<GPUChar>) { // TODO: NN buffer
    val CHARW = 8
    val CHARH = 16

    private var texwidth: Int = width*CHARW;
    private var texheight: Int = height*CHARH;
    private var image: NativeImage = NativeImage(texwidth, texheight, true); // idk what the boolean is
    private var tex: DynamicTexture = DynamicTexture(image)

    init {
        Minecraft.getInstance().textureManager.register(this.id, tex)
    }

    fun dump(path: String) {
        image.writeToFile(File(path))
        NeoComputers.LOGGER.info("DUMPED!!!")
    }

//    fun toRGBA(color: Int): Int {
//        return color.shl(8).or(0xFF)
//    }

    fun drawGlyph(x: Int, y: Int, c: Char, fg: Int) {
        var glyph: ArrayList<Byte> = FontProvider.map[c]!!

        for (j in 0..<CHARH) {
            for (i in 0..<CHARW) {
                // var pixel = ((glyph[j] and ((1 shl (CHARW - i - 1)).toByte())).toInt()) ushr (CHARW - i - 1) // retardation
                var pixel = (glyph[j] and (0b10000000 ushr i).toByte()).toInt()
                if (pixel > 0) image.setPixelRGBA(x+i, y+j, 0xFF000000.toInt()+fg)
            }
        }
    }

    fun drawBuffer() {
        for (i in 0..<width) {
            for (j in 0..<height) {
                var char: GPUChar = get(i, j)
                var x = i*CHARW
                var y = j*CHARH
                image.fillRect(x, y, CHARW, CHARH, (0xFF000000+char.bg).toInt())
                if (char.c != ' ' && char.c != '\u0000') drawGlyph(x, y, char.c, char.fg) 
            }
        }
        tex.upload()
    }
    
    fun get(x: Int, y: Int) = buffer[y*width+x]
    fun set(x: Int, y: Int, c: GPUChar) {
        buffer[y*width+x] = c
    }

    fun clean() {
        Minecraft.getInstance().textureManager.release(this.id)
        image.close()
        tex.close()
    }

    data class GPUChar(val c: Char, val fg: Int =0xFFFFFF, val bg: Int = 0) // all is bgr
}