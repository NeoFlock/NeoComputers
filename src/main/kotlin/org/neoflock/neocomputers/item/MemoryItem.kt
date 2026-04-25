package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.Formatting

open class MemoryItem(val tier: Int, val capacity: Int): Item(Item.Properties().`arch$tab`(Tabs.TAB)), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack) = setOf(ComponentRoles.MEMORY)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getMemoryCapacity(itemStack: ItemStack): Int = capacity

    override fun getComponentCapacity(itemStack: ItemStack): Int = 0

    // no node for memory
    override fun toComponentNode(itemStack: ItemStack, machine: MachineEntity?): Networking.Node? = null

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipFlag.isAdvanced) {
            list.addLast(Component.translatable("neocomputers.memory.capacity", Formatting.formatMemory(capacity.toLong())))
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}

class MemoryTier1(): MemoryItem(1, 192 shl 10)
class MemoryTier1_5(): MemoryItem(1, 256 shl 10)
class MemoryTier2(): MemoryItem(2, 384 shl 10)
class MemoryTier2_5(): MemoryItem(2, 512 shl 10)
class MemoryTier3(): MemoryItem(3, 768 shl 10)
class MemoryTier3_5(): MemoryItem(3, 1 shl 20)