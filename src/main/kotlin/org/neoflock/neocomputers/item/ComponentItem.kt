package org.neoflock.neocomputers.item

import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.network.Networking

// need not necessarily be just a component, can be upgrades as well
interface ComponentItem {
    fun getComponentRoles(itemStack: ItemStack): Set<String>
    fun getComponentTier(itemStack: ItemStack): Int

    // Get machine properties they can influence
    fun getMemoryCapacity(itemStack: ItemStack): Int
    fun getComponentCapacity(itemStack: ItemStack): Int

    fun whenComponentPlaced(itemStack: ItemStack, newRole: String)
    fun whenComponentTaken(itemStack: ItemStack, previousRole: String)

    // To node, if applicable
    fun toComponentNode(itemStack: ItemStack): Networking.Node?
}