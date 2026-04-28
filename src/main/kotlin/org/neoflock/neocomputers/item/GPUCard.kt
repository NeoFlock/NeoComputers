package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.widget.ComponentRoles

// Note: We'll prob want to replace them with NN component configs later on

open class GPUCard(val tier: Int, val vram: Long): Item(Properties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.CARD)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun whenComponentPlaced(itemStack: ItemStack, machine: ComponentUser?, newRole: String) {
        if(machine != null) ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, machine, newRole)
    }

    // TODO: GPU Component
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
            list.addLast(Component.translatable("neocomputers.gpu.vram", vram))
            // TODO: show VRAM usage and whatnot
        }
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag)
    }
}

class GPUCard0: GPUCard(1, 5000)
class GPUCard1: GPUCard(2, 10000)
class GPUCard2: GPUCard(3, 20000)