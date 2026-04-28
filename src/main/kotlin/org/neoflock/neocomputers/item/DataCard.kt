package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.utils.Formatting

// Note: We'll prob want to replace them with NN component configs later on

open class DataCard(val tier: Int, val limit: Long): Item(Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.CARD)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun whenComponentPlaced(itemStack: ItemStack, machine: ComponentUser?, newRole: String) {
        if(machine != null) ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, machine, newRole)
    }

    // TODO: Modem Component
    override fun toComponentNode(itemStack: ItemStack, machine: ComponentUser?) = null

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipFlag.isAdvanced) {
            val addr = itemStack.get(DataComponents.ADDRESS)
            val addrComp = if(addr == null) Component.translatable("neocomputers.noaddr") else Component.literal(addr)
            list.addLast(addrComp)
            list.addLast(Component.translatable("neocomputers.data.limit", Formatting.formatMemory(limit)))
            // TODO: show encryption support
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}

class DataCard0: DataCard(1, 1 shl 20)
class DataCard1: DataCard(2, 1 shl 20)
class DataCard2: DataCard(3, 1 shl 20)