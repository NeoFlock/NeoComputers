package org.neoflock.neocomputers.item

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers
import java.nio.ByteBuffer

object DataComponents {
    val ADDRESS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "address"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
    val LABEL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "label"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
    val READONLY = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "readonly"),
        DataComponentType.builder<Boolean>().persistent(Codec.BOOL).build())
    val EEPROM_CODE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_code"),
        DataComponentType.builder<ByteBuffer>().persistent(Codec.BYTE_BUFFER).build())
    val EEPROM_DATA = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_data"),
        DataComponentType.builder<ByteBuffer>().persistent(Codec.BYTE_BUFFER).build())
    val EEPROM_CODESIZE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_codesize"),
        DataComponentType.builder<Int>().persistent(Codec.INT).build())
    val EEPROM_DATASIZE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_datasize"),
        DataComponentType.builder<Int>().persistent(Codec.INT).build())

    val TUNNEL_CHANNEL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "tunnel_channel"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
}