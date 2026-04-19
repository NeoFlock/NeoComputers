package org.neoflock.neocomputers.item

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.NeoComputers

object Tabs {
    val TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(NeoComputers.MODID, Registries.CREATIVE_MODE_TAB)

    val TAB: CreativeModeTab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
        ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "neocomputers_tab"), CreativeTabRegistry.create {
        builder ->
        builder.title(Component.literal("NeoComputers"))
        builder.icon {
            ItemStack(Items.MEM0)
        }
        builder.displayItems {
            parameters, output ->
            output.accept(ItemStack(Items.EE0))

            val luaBios = ItemStack(Items.EE0)
            luaBios.set(DataComponents.LABEL, "Lua BIOS")
            luaBios.set(DataComponents.EEPROM_CODE, "error('hi')")
            luaBios.set(DataComponents.EEPROM_DATA, "random garbage")
            output.accept(luaBios)
        }
    })
}