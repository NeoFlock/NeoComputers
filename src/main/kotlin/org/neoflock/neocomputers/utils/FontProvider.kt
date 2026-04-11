package org.neoflock.neocomputers.utils;

import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import org.neoflock.neocomputers.NeoComputers
import java.nio.charset.StandardCharsets

object FontProvider {
    val map: MutableMap<Char, ArrayList<Byte>> = mutableMapOf();

    fun load(loc: Identifier) { // TODO: optimize, this can totally be optimized
        var man: ResourceManager = Minecraft.getInstance().resourceManager
        var resource: Resource = man.getResourceOrThrow(loc)
        var stream = resource.open()
        while (stream.available() > 0) {
            var key = Integer.parseInt(String(stream.readNBytes(5), StandardCharsets.UTF_8), 16).toChar()
            stream.skip(1)
            var bytes: ArrayList<Byte> = ArrayList<Byte>();
            while (true) { // shut up will you
                var b1 = stream.read()
                if (b1 == 10) break // 10 is line break
                var b2 = stream.read() 
                var value: Byte = Integer.parseInt(arrayOf(b1.toChar(), b2.toChar()).joinToString(""), 16).toByte()
                bytes.add(value)
            } 
            map[key] = bytes
        }
        NeoComputers.LOGGER.info("[FontProvider] Loaded font!");
    }
}