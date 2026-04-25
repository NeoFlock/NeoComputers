package org.neoflock.neocomputers.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking

open class CBUSItem(val tier: Int, val maxComponents: Int): Item(Item.Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.BUS)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getComponentCapacity(itemStack: ItemStack): Int = maxComponents

    override fun toComponentNode(itemStack: ItemStack, machine: MachineEntity?): Networking.Node? = null
}
class CBUS0: CBUSItem(1, 8)
class CBUS1: CBUSItem(2, 12)
class CBUS2: CBUSItem(3, 16)
class CBUSCreative: CBUSItem(1, 1024)
