package org.neoflock.neocomputers.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.Formatting
import java.util.UUID

fun getEEPROMProperties(): Item.Properties = Item.Properties()
    .component(DataComponents.EEPROM_CODE, "")
    .component(DataComponents.EEPROM_DATA, "")
    .component(DataComponents.LABEL, "")
    .component(DataComponents.READONLY, false)

open class EEPROMItem(val tier: Int, val codeCapacity: Int, val dataCapacity: Int): Item(getEEPROMProperties()), ComponentItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.FIRMWARE)

    override fun getComponentTier(itemStack: ItemStack): Int = tier

    override fun getMemoryCapacity(itemStack: ItemStack): Int = 0

    override fun getComponentCapacity(itemStack: ItemStack): Int = 0

    override fun whenComponentPlaced(itemStack: ItemStack, machine: MachineEntity, newRole: String) {
        ensureHasAddress(itemStack)
        super.whenComponentPlaced(itemStack, machine, newRole)
    }

    override fun toComponentNode(itemStack: ItemStack, machine: MachineEntity): Networking.Node? = null

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipFlag.isAdvanced) {
            val code = itemStack.get(DataComponents.EEPROM_CODE) ?: ""
            val data = itemStack.get(DataComponents.EEPROM_DATA) ?: ""
            val addr = itemStack.get(DataComponents.ADDRESS)
            val readonly = itemStack.get(DataComponents.READONLY) ?: false
            val codeSize = code.encodeToByteArray().size
            val dataSize = data.encodeToByteArray().size
            val addrComp = if(addr == null) Component.translatable("neocomputers.noaddr") else Component.literal(addr)
            list.addLast(addrComp)
            list.addLast(Component.translatable("neocomputers.eeprom.codeused", Formatting.formatMemory(codeSize.toLong()),
                Formatting.formatMemory(codeCapacity.toLong())))
            list.addLast(Component.translatable("neocomputers.eeprom.dataused", Formatting.formatMemory(dataSize.toLong()),
                Formatting.formatMemory(dataCapacity.toLong())))
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
}

class EEPROM0: EEPROMItem(1, 4096, 256)