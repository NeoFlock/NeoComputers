package org.neoflock.neocomputers.item;

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier


object Tabs {
    val TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(NeoComputers.MODID, Registries.CREATIVE_MODE_TAB)

    val TAB: RegistrySupplier<CreativeModeTab> = TABS.register("neocomputers_tab", Supplier {CreativeTabRegistry.create( 
        Component.literal("NeoComputers"),
        Supplier { ItemStack(Items.ACACIA_BOAT) }
     )})
}