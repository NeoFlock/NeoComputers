package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking

// Note: We'll prob want to replace them with NN component configs later on

open class RedstoneCard(val tier: Int): Item(Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.CARD)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun whenComponentPlaced(itemStack: ItemStack, machine: MachineEntity?, newRole: String) {
        if(machine != null) ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, machine, newRole)
    }

    // TODO: Redstone Component
    override fun toComponentNode(itemStack: ItemStack, machine: MachineEntity?) = null

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipFlag.isAdvanced) {
            val addr = itemStack.get(DataComponents.ADDRESS)
            val addrComp = if(addr == null) Component.translatable("neocomputers.noaddr") else Component.literal(addr)
            // TODO: show redstone and whatnot
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}

class RedstoneCard0: RedstoneCard(1)
class RedstoneCard1: RedstoneCard(2)