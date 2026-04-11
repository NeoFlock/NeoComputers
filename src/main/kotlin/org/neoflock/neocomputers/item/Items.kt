package org.neoflock.neocomputers.item;

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import org.neoflock.neocomputers.NeoComputers

object Items {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(NeoComputers.MODID, Registries.ITEM)
}