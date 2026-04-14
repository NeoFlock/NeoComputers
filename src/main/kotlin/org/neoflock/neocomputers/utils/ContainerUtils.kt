package org.neoflock.neocomputers.utils

import dev.architectury.registry.fuel.FuelRegistry
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

// mewhenthe, aka e, will have me publicly executed for this code
object ContainerUtils {
    fun getBurningTime(itemStack: ItemStack): Int? {
        val time = FuelRegistry.get(itemStack)
        if(time == 0) return null
        return time
    }

    fun isBurningFuel(itemStack: ItemStack) = getBurningTime(itemStack) != null
}