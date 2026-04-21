package org.neoflock.neocomputers.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking

open class CPUItem(val tier: Int, val maxComponents: Int): Item(Item.Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.COMPUTE)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getComponentCapacity(itemStack: ItemStack): Int = maxComponents

    override fun toComponentNode(itemStack: ItemStack, machine: MachineEntity): Networking.Node? = null
}

class CPU0: CPUItem(1, 8)
class CPU1: CPUItem(2, 12)
class CPU2: CPUItem(3, 16)