package org.neoflock.neocomputers.item

import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.network.Networking
import java.util.UUID

// need not necessarily be just a component, can be upgrades as well
interface ComponentItem {
    fun getComponentRoles(itemStack: ItemStack): Set<String>
    fun getComponentTier(itemStack: ItemStack): Int

    // Get machine properties they can influence
    fun getMemoryCapacity(itemStack: ItemStack): Int = 0
    fun getComponentCapacity(itemStack: ItemStack): Int = 0

    // Component placed, node must now exist
    fun whenComponentPlaced(itemStack: ItemStack, newRole: String) {
        val node = toComponentNode(itemStack) ?: return
        Networking.addNode(node)
    }

    // Component taken, and thus removed
    fun whenComponentTaken(itemStack: ItemStack, previousRole: String) {
        val node = toComponentNode(itemStack) ?: return
        Networking.removeNode(node)
    }

    // To node, if applicable
    fun toComponentNode(itemStack: ItemStack): Networking.Node?

    fun ensureHasAddress(itemStack: ItemStack): UUID {
        if(!itemStack.has(DataComponents.ADDRESS)) {
            itemStack.set(DataComponents.ADDRESS, UUID.randomUUID().toString())
        }
        return UUID.fromString(itemStack.get(DataComponents.ADDRESS))
    }
}