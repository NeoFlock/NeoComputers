package org.neoflock.neocomputers.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.widget.ComponentRoles

open class CPUItem(val tier: Int, val maxComponents: Int): Item(Item.Properties()), RelayUpgrade {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.COMPUTE)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getComponentCapacity(itemStack: ItemStack): Int = maxComponents

    override fun getArchitecturesProvided(itemStack: ItemStack): Set<String> = setOf("Lua 5.3")

    override fun toComponentNode(itemStack: ItemStack, machine: ComponentUser?) = null

    override fun getRelayInterval(itemStack: ItemStack) = 4 / tier
}

class CPU0: CPUItem(1, 8)
class CPU1: CPUItem(2, 12)
class CPU2: CPUItem(3, 16)