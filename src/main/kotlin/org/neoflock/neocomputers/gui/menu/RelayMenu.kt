package org.neoflock.neocomputers.gui.menu

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.RelayEntity
import org.neoflock.neocomputers.gui.widget.ComponentRoles
import org.neoflock.neocomputers.gui.widget.DynamicSlot
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.item.RelayUpgrade
import org.neoflock.neocomputers.utils.GenericContainerMenu

class RelaySlot(container: Container, slot: Int, x: Int, y: Int, val role: String, val relay: RelayEntity?) : DynamicSlot(container, slot, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean {
        if(stack.isEmpty) return true
        val upgrade = stack.item as? RelayUpgrade ?: return false
        if(containerSlot == RelayEntity.CARD) {
            return upgrade.isRelayCompatibleCard(stack)
        }
        if(containerSlot == RelayEntity.CPU) {
            return upgrade.getRelayInterval(stack) != null
        }
        if(containerSlot == RelayEntity.MEM) {
            return upgrade.getRelayBufferSize(stack) != null
        }
        if(containerSlot == RelayEntity.STORAGE) {
            return upgrade.getRelayQueueSize(stack) != null
        }
        return false
    }

    override fun set(stack: ItemStack) {
        super.set(stack)
        val item = stack.item
        if(item is ComponentItem) {
            item.whenComponentPlaced(stack, relay, role)
        }
    }

    override fun onTake(player: Player, stack: ItemStack) {
        super.onTake(player, stack)
        val item = stack.item
        if(item is ComponentItem) {
           item.whenComponentTaken(stack, relay, role)
        }
    }

    override fun getMaxStackSize() = 1
    override fun getMaxStackSize(stack: ItemStack) = 1

    override fun draw(graphics: GuiGraphics, relX: Int, relY: Int, mouseX: Int, mouseY: Int) {
        super.draw(graphics, relX, relY, mouseX, mouseY)
        if(!hasItem()) {
            RenderSystem.enableBlend()
            RenderSystem.setShaderTexture(0, ComponentRoles.getTextureFor(role))
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            drawQuad(relX + x - 1, relY + y - 1, 18, 18, 0F, 0F, 15F, 15F)
            RenderSystem.disableBlend()
        }
    }
}

class RelayMenu : GenericContainerMenu {
    constructor(i: Int, inv: Inventory) : this(i, inv, SimpleContainer(RelayEntity.SLOT_COUNT))

    constructor(i: Int, inv: Inventory, container: Container) : super(Menus.RELAY_MENU.get(), i, container) {
        val relay = container as? RelayEntity

        val relayMenuWidth = 176
        val itemX = 152
        val itemRowDist = 16
        val itemSpacing = 20

        this.addSlot(RelaySlot(container, RelayEntity.CARD, relayMenuWidth+4, itemRowDist, ComponentRoles.NETWORK, relay))

        this.addSlot(RelaySlot(container, RelayEntity.CPU, itemX, itemRowDist, ComponentRoles.COMPUTE, relay))
        this.addSlot(RelaySlot(container, RelayEntity.MEM, itemX, itemRowDist+itemSpacing, ComponentRoles.MEMORY, relay))
        this.addSlot(RelaySlot(container, RelayEntity.STORAGE, itemX, itemRowDist+itemSpacing*2, ComponentRoles.STORAGE, relay))

        this.addInventorySlots(inv, 8, 84)
    }
}