package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking

open class NetworkCard(val tier: Int, val maxRange: Int, val isWired: Boolean): Item(Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.CARD, ComponentRoles.NETWORK)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun whenComponentPlaced(itemStack: ItemStack, newRole: String) {
        ensureHasAddress(itemStack)
    }

    // TODO: Modem Component
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
            if(maxRange > 0) {
                list.addLast(Component.translatable("neocomputers.wlan.range", maxRange))
            }
            // TODO: show max packet size and whatnot
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}

class LANCard: NetworkCard(1, 0, true)
class WLANCard0: NetworkCard(1, 16, true)
class WLANCard1: NetworkCard(1, 400, true)