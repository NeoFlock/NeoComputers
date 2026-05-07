package org.neoflock.neocomputers.item

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.network.DeviceNode

class ServerItem() : Item(Properties()), ComponentItem, RackItem {
    override fun getComponentRoles(itemStack: ItemStack): Set<String> = setOf(ComponentRoles.RACK_MOUNTABLE)

    override fun getComponentTier(itemStack: ItemStack): Int = 0

    override fun toComponentNode(
        itemStack: ItemStack,
        machine: ComponentUser?
    ): DeviceNode? {
        return null // TODO: atom machine item plz
    }

    override fun render_lights(source: MultiBufferSource, stack: PoseStack, light: Int) {
    }
}