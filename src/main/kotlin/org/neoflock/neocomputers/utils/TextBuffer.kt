package org.neoflock.neocomputers.utils

import net.minecraft.network.FriendlyByteBuf
import kotlin.math.min

data class GPUChar(val c: Char, val fg: Int =0xFFFFFF, val bg: Int = 0) // all is bgr

// TODO: wrapper over NN buffer
class TextBuffer(var width: Int, var height: Int) {
    val blank = GPUChar(' ')
    var buf = Array(width*height) { blank }

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
    fun set(x: Int, y: Int, pixel: GPUChar) {
        if(!inBounds(x, y)) return
        buf[x+y*width] = pixel
    }
    fun set(x: Int, y: Int, text: String, fg: Int = 0xFFFFFF, bg: Int = 0x000000, vertical: Boolean = false) {
        for ((i, c) in text.toCharArray().withIndex()) {
            val cx = if(vertical) x else x + i
            val cy = if(vertical) y + i else y
            set(cx, cy, GPUChar(c, fg, bg))
        }
    }
    fun fill(x: Int, y: Int, w: Int, h: Int, pixel: GPUChar = blank) {
        // turn it into values we can fw
        val fw = min(w, width)
        val fh = min(h, height)
        for(py in y..<y+fh) {
            for (px in x..<x + fw) {
                set(px, py, pixel)
            }
        }
    }
}