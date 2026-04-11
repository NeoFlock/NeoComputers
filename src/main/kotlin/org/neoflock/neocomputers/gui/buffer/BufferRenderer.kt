package org.neoflock.neocomputers.gui.buffer;

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.resources.Identifier
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.utils.FontProvider
import java.io.File
import kotlin.experimental.and
import kotlin.experimental.xor

class BufferRenderer(width: Int, height: Int, id: Identifier, buffer: MutableList<GPUChar>) { // TODO: NN buffer
    val CHARW = 8
    val CHARH = 16

    private var width: Int = width;
    private var height: Int = height;
    private var id: Identifier = id;
    private var buffer: MutableList<GPUChar> = buffer;

    private var texwidth: Int = width*CHARW;
    private var texheight: Int = height*CHARH;
    private var image: NativeImage = NativeImage(texwidth, texheight, true); // idk what the boolean is
    private var tex: DynamicTexture = DynamicTexture({id.path}, image)

    fun dump(path: String) {
        image.writeToFile(File(path))
        NeoComputers.LOGGER.info("DUMPED!!!")
    }

    fun drawGlyph(x: Int, y: Int, c: Char, fg: Int) {
        var glyph: ArrayList<Byte> = FontProvider.map[c]!!

        for (j in 0..<CHARH) {
            for (i in 0..<CHARW) {
                // var pixel = ((glyph[j] and ((1 shl (CHARW - i - 1)).toByte())).toInt()) ushr (CHARW - i - 1) // retardation
                var pixel = (glyph[j] and (0b10000000 ushr i).toByte()).toInt()
                if (pixel > 0) image.setPixelABGR(x+i, y+j, (0xFF000000+fg).toInt())
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

    fun register() {  // i would love to do this in the constructor but kotlin quirks blahblah
        Minecraft.getInstance().textureManager.register(this.id, tex) // also idk how to unregister this
    }

    fun clean() {
        image.close()
        tex.close()
        Minecraft.getInstance().textureManager.release(this.id)
    }

    data class GPUChar(val c: Char, val fg: Int =0xFFFFFF, val bg: Int = 0)
}