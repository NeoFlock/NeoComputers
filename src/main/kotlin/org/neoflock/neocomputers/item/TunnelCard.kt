package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.Formatting

class TunnelCard: Item(Properties()), ComponentItem {
    // yes, we're counting TUNNEL as a conventional networking card
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.CARD, ComponentRoles.NETWORK)

    override fun getComponentTier(itemStack: ItemStack): Int = 3

    override fun whenComponentPlaced(itemStack: ItemStack, newRole: String) {
        ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, newRole)
    }

    // TODO: Tunnel Component
    override fun toComponentNode(itemStack: ItemStack): Networking.Node? = null

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
            // TODO: show max packet size and whatnot
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}