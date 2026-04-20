package org.neoflock.neocomputers.utils;

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import org.neoflock.neocomputers.NeoComputers
import java.nio.charset.StandardCharsets

/*
* OC hex font format:
* 5 character hex code .. ":" .. variable length hex code .. LF
* this is essentially a dictionary
* */
object FontProvider {
    val map: MutableMap<Char, ArrayList<Byte>> = mutableMapOf();

    fun load(loc: ResourceLocation) {
        var man: ResourceManager = Minecraft.getInstance().resourceManager
        var resource: Resource = man.getResourceOrThrow(loc)
        var stream = resource.open()
        var bfr = stream.bufferedReader();
        while (stream.available() > 0) {
            var line: String = bfr.readLine()
            var splitLine = line.split(":");
            var key = splitLine[0].hexToInt().toChar();
            var value: ByteArray = splitLine[1].hexToByteArray(); // shout out to the kotlin stdlib for having ts
            var bytes: ArrayList<Byte> = value.toCollection(ArrayList<Byte>());
            map[key] = bytes
        }
        NeoComputers.LOGGER.info("[FontProvider] Loaded font!");
    }
}