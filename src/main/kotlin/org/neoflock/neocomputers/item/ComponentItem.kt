package org.neoflock.neocomputers.item

import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.entity.MachineEvent
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import java.util.UUID

// need not necessarily be just a component, can be upgrades as well
interface ComponentItem {
    fun getComponentRoles(itemStack: ItemStack): Set<String>
    fun getComponentTier(itemStack: ItemStack): Int

    // Get machine properties they can influence
    fun getMemoryCapacity(itemStack: ItemStack): Int = 0
    fun getComponentCapacity(itemStack: ItemStack): Int = 0
    fun getArchitecturesProvided(itemStack: ItemStack): Set<String> = setOf()

    // Component placed, node must now exist
    fun whenComponentPlaced(itemStack: ItemStack, machine: ComponentUser?, newRole: String) {
        val oldNode = getComponentNode(itemStack)
        if(oldNode != null) Networking.removeNode(oldNode) // did a mod forget to call whenComponentTaken?
        val node = toComponentNode(itemStack, machine) ?: return
        Networking.addNode(node)
        machine?.getMachineNode()?.connectTo(node)
    }

    // Component taken, and thus removed
    fun whenComponentTaken(itemStack: ItemStack, machine: ComponentUser?, previousRole: String) {
        val node = getComponentNode(itemStack) ?: return
        // removing disconnects
        Networking.removeNode(node)
    }

    // To node, if applicable. Meant to create the node, but not add it, as it will use the itemStack's address to find it again
    fun toComponentNode(itemStack: ItemStack, machine: ComponentUser?): DeviceNode?

    // Gets the node associated to an item, if it exists
    fun getComponentNode(itemStack: ItemStack): DeviceNode? {
        val address = itemStack.get(DataComponents.ADDRESS) ?: return null
        val uuid = UUID.fromString(address) ?: return null
        return Networking.getNode(uuid)
    }

    fun ensureHasAddress(itemStack: ItemStack): UUID {
        if(!itemStack.has(DataComponents.ADDRESS)) {
            itemStack.set(DataComponents.ADDRESS, UUID.randomUUID().toString())
        }
        return UUID.fromString(itemStack.get(DataComponents.ADDRESS))
    }

    fun onMachineEvent(itemStack: ItemStack, machine: MachineEntity, event: MachineEvent) {}
}

// A special ComponentItem which specifies upgrades specific to the relay
interface RelayUpgrade: ComponentItem {
    fun getRelayInterval(itemStack: ItemStack): Int? = null
    fun getRelayBufferSize(itemStack: ItemStack): Int? = null
    fun getRelayQueueSize(itemStack: ItemStack): Int? = null
    fun isRelayCompatibleCard(itemStack: ItemStack): Boolean = false
}