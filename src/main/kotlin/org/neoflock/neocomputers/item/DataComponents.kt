package org.neoflock.neocomputers.item

import com.mojang.serialization.Codec
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers

object DataComponents {
    val ADDRESS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "address"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
    val LABEL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "label"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
    val EEPROM_CODE = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_code"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
    val EEPROM_DATA = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "eeprom_data"),
        DataComponentType.builder<String>().persistent(Codec.STRING).build())
}