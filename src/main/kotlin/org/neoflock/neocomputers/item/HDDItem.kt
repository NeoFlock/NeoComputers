package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.utils.Formatting

fun getDiskProperties(): Item.Properties = Item.Properties()
    .component(DataComponents.LABEL, "")
    .component(DataComponents.READONLY, false)

open class HardDiskItem(val tier: Int, val capacity: Long, val relayQueueSize: Int): Item(getDiskProperties()), RelayUpgrade {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.STORAGE)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getMemoryCapacity(itemStack: ItemStack): Int = 0

    override fun getComponentCapacity(itemStack: ItemStack): Int = 0

    override fun whenComponentPlaced(itemStack: ItemStack, machine: ComponentUser?, newRole: String) {
        if(machine != null) ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, machine, newRole)
    }

    override fun toComponentNode(itemStack: ItemStack, machine: ComponentUser?) = null

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipFlag.isAdvanced) {
            val addr = itemStack.get(DataComponents.ADDRESS)
            val readonly = itemStack.get(DataComponents.READONLY) ?: false
            val spaceUsed: Long = 0
            val addrComp = if(addr == null) Component.translatable("neocomputers.noaddr") else Component.literal(addr)
            list.addLast(addrComp)
            list.addLast(Component.translatable("neocomputers.disk.spaceused", Formatting.formatMemory(spaceUsed),
                Formatting.formatMemory(capacity)))
            list.addLast(Component.translatable(if(readonly) "neocomputers.readonly" else "neocomputers.readwrite"))
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }

    override fun getName(itemStack: ItemStack): Component? {
        if(itemStack.has(DataComponents.LABEL)) {
            val label = itemStack.get(DataComponents.LABEL) ?: ""
            if(label.isNotEmpty()) return Component.literal(label)
        }
        return super.getName(itemStack)
    }

    override fun getRelayQueueSize(itemStack: ItemStack) = relayQueueSize
}

class HardDisk0: HardDiskItem(1, 1 shl 20, 30)
class HardDisk1: HardDiskItem(2, 2 shl 20, 40)
class HardDisk2: HardDiskItem(3, 4 shl 20, 50)