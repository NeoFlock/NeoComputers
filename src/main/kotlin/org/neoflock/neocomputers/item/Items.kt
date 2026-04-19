package org.neoflock.neocomputers.item

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier

object Items {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(NeoComputers.MODID, Registries.ITEM)

    val MEM0 = ITEMS.register("memory0") { MemoryTier1() }
    val MEM1 = ITEMS.register("memory1") { MemoryTier1_5() }
    val MEM2 = ITEMS.register("memory2") { MemoryTier2() }
    val MEM3 = ITEMS.register("memory3") { MemoryTier2_5() }
    val MEM4 = ITEMS.register("memory4") { MemoryTier3() }
    val MEM5 = ITEMS.register("memory5") { MemoryTier3_5() }

    val EE0 = ITEMS.register("eeprom0") { EEPROM0() }
}