package org.neoflock.neocomputers.utils

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.ResourceLocationPattern
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import kotlin.experimental.and
import kotlin.math.min

data class GPUChar(val c: Char, val fg: Int =0xFFFFFF, val bg: Int = 0) // all is bgr

// TODO: wrapper over NN buffer
class TextBuffer(var width: Int, var height: Int) {
//    val CHARW = 8
//    val CHARH = 16
//
//    val texwidth: Int
//        get() = width*CHARW
//
//    val texheight: Int
//        get() = height*CHARH
//
//    var image = NativeImage(texwidth, texheight, true)
//    var tex = DynamicTexture(image)


    val blank = GPUChar(' ')
    var buf = Array(width*height) { blank }
//    init {
//        Minecraft.getInstance().textureManager.register(this.id, tex)
//    }

//    fun toRGBA(color: Int): Int {
//        // Minecaft lies, its AGBR
//        return java.lang.Integer.reverseBytes((color.toLong() * 256 + 0xFF).toInt())
//    }
//
//    fun drawGlyph(x: Int, y: Int, c: Char, fg: Int) {
//        var glyph: ArrayList<Byte> = FontProvider.map[c]!!
//
//        for (j in 0..<CHARH) {
//            for (i in 0..<CHARW) {
//                // var pixel = ((glyph[j] and ((1 shl (CHARW - i - 1)).toByte())).toInt()) ushr (CHARW - i - 1) // retardation
//                var pixel = (glyph[j] and (0b10000000 ushr i).toByte()).toInt()
//                if (pixel > 0) image.setPixelRGBA(x+i, y+j, toRGBA(fg))
//            }
//        }
//    }
//
//    fun drawBuffer() {
//        for (i in 0..<width) {
//            for (j in 0..<height) {
//                var char: GPUChar = buf[j*height+i]
//                var x = i*CHARW
//                var y = j*CHARH
//                image.fillRect(x, y, CHARW, CHARH, toRGBA(char.bg))
//                if (char.c != ' ' && char.c != '\u0000') drawGlyph(x, y, char.c, char.fg)
//            }
//        }
//        tex.upload()
//    }
//
//    fun clean() {
//        Minecraft.getInstance().textureManager.release(this.id)
//        image.close()
//        tex.close()
//    }

    fun encodeContents(buf: FriendlyByteBuf) {
        // 0x01 means set fg, 0x02 means set bg,
        // 0x03 means set char+count
        var lastFg = 0xFFFFFF
        var lastBg = 0x000000

        buf.writeVarInt(width)
        buf.writeVarInt(height)

        var i = 0
        while(i < this.buf.size) {
            val px = this.buf[i]
            if(px.fg != lastFg) {
                buf.writeByte(0x01)
                buf.writeVarInt(px.fg)
                lastFg = px.fg
            }
            if(px.bg != lastBg) {
                buf.writeByte(0x02)
                buf.writeVarInt(px.bg)
                lastBg = px.bg
            }
            var charWritten = 1
            while((i+charWritten) < this.buf.size && this.buf[i+charWritten].c == px.c) charWritten++
            buf.writeByte(0x03)
            buf.writeVarInt(px.c.code)
            buf.writeVarInt(charWritten)
            i += charWritten
        }
    }

    fun decodeContents(buf: FriendlyByteBuf) {
        var lastFg = 0xFFFFFF
        var lastBg = 0x000000

        width = buf.readVarInt()
        height = buf.readVarInt()

        if(width*height != this.buf.size) {
            this.buf = Array(width * height) { blank }
        }

        var i = 0
        while(i < width*height) {
            val op = buf.readByte().toInt()
            if(op == 0x01) {
                lastFg = buf.readVarInt()
            }
            if(op == 0x02) {
                lastBg = buf.readVarInt()
            }
            if(op == 0x03) {
                val c = buf.readVarInt().toChar()
                val n = buf.readVarInt()

                for(o in 0..<n) {
                    this.buf[i+o] = GPUChar(c, lastFg, lastBg)
                }
                i += n
            }
        }
    }

    fun inBounds(x: Int, y: Int) = x >= 0 && y >= 0 && x < width && y < height
    fun get(x: Int, y: Int) = if(inBounds(x, y)) buf[x+y*width] else blank
    fun _set(x: Int, y: Int, pixel: GPUChar) {
        if(!inBounds(x, y)) return
        buf[x+y*width] = pixel
//        image.fillRect(x, y, CHARW, CHARH, toRGBA(pixel.bg))
//        if (pixel.c != ' ' && pixel.c != '\u0000') drawGlyph(x, y, pixel.c, pixel.fg)
    }
    fun set(x: Int, y: Int, text: String, fg: Int = 0xFFFFFF, bg: Int = 0x000000, vertical: Boolean = false) {
        for ((i, c) in text.toCharArray().withIndex()) {
            val cx = if(vertical) x else x + i
            val cy = if(vertical) y + i else y
            _set(cx, cy, GPUChar(c, fg, bg))
        }
//        tex.upload()
    }
    fun fill(x: Int, y: Int, w: Int, h: Int, pixel: GPUChar = blank) {
        // turn it into values we can fw
        val fw = min(w, width)
        val fh = min(h, height)
        for(py in y..<y+fh) {
            for (px in x..<x + fw) {
                _set(px, py, pixel)
            }
        }
//        tex.upload()
    }
}