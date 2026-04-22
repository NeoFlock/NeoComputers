package org.neoflock.neocomputers.utils

// based off the ImplementedContainer of https://docs.fabricmc.net/develop/blocks/block-containers
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.Container;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.gui.widget.DynamicSlot

// Common container interface, assumes the entire purpose is purely raw item storage
interface GenericContainer : Container {
    fun getItems(): NonNullList<ItemStack>

    override fun getContainerSize(): Int {
        return getItems().size
    }

    override fun isEmpty(): Boolean {
        return getItems().all { it.isEmpty }
    }

    override fun getItem(i: Int): ItemStack {
        return getItems()[i]
    }

    override fun removeItem(slot: Int, count: Int): ItemStack {
        val res = ContainerHelper.removeItem(getItems(), slot, count)
        if (!res.isEmpty) setChanged()
        return res
    }

    override fun setItem(slot: Int, itemStack: ItemStack) {
        getItems()[slot] = itemStack

        // in case of bullshit
        if(itemStack.count > itemStack.maxStackSize) {
            // rip items
            itemStack.count = itemStack.maxStackSize
        }
    }

    override fun removeItemNoUpdate(i: Int): ItemStack {
        return ContainerHelper.takeItem(getItems(), i)
    }

    override fun clearContent() {
        getItems().clear()
    }
}

abstract class GenericContainerMenu(menuType: MenuType<*>, id: Int, var container: Container): AbstractContainerMenu(menuType, id) {
    fun addInventorySlots(inventory: Inventory, x: Int, y: Int) {
        // Based off the code in ChestMenu
        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlot(Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
            }
        }

        addInventoryHotbar(inventory, x, y + 3 * 18 + 4)
    }

    fun addInventoryHotbar(inventory: Inventory, x: Int, y: Int) {
        for (i in 0..8) {
            this.addSlot(Slot(inventory, i, x + i * 18, y))
        }
    }

    // taken from https://docs.fabricmc.net/develop/blocks/container-menus
    override fun quickMoveStack(player: Player, i: Int): ItemStack? {
        val slot = slots[i]

        if(!slot.hasItem()) return ItemStack.EMPTY

        val stack = slot.item
        val copied = stack.copy()
        val contSize = container.containerSize

        if(i < contSize) {
            if(!this.moveItemStackTo(stack, contSize, slots.size, true)) {
                return ItemStack.EMPTY
            }
        } else if(!this.moveItemStackTo(stack, 0, contSize, false)) {
            return ItemStack.EMPTY
        }

        if(stack.isEmpty) {
            slot.setByPlayer(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }

        return copied
    }

    override fun stillValid(player: Player): Boolean {
        return container.stillValid(player)
    }
}

abstract class GenericContainerScreen<T: GenericContainerMenu>(menu: T, inventory: Inventory, component: Component): AbstractContainerScreen<T>(menu, inventory, component) {
    open fun shouldCenterTitle() = true
    open fun shouldRenderTooltip() = true
    open fun findMenuTexture(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/background.png")

    open fun getBoundBlockEntityType(): Set<BlockEntityType<*>> = setOf()

    open fun processScreenStatePacket(buf: FriendlyByteBuf) {}

    val imageX: Int
        get() = (width - imageWidth) / 2

    val imageY: Int
        get() = (height - imageHeight) / 2

    override fun init() {
        super.init()

        if(shouldCenterTitle()) this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2
    }

    override fun renderBg(guiGraphics: GuiGraphics, f: Float, i: Int, j: Int) {
        val menuTex = findMenuTexture()
        val cx = (width - imageWidth) / 2
        val cy = (height - imageHeight) / 2

        guiGraphics.blit(menuTex, imageX, imageY, 0, 0, imageWidth, imageHeight)

        for (slot in menu.slots) {
            if (slot is DynamicSlot) {
                // NeoComputers.LOGGER.info("slot")
                slot.draw(guiGraphics, cx, cy, i, j)
            }
        }
    }

    open fun renderCustomOverlay(graphics: GuiGraphics, mouseX: Int, mouseY: Int, blend: Float) {

    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, something: Float) {
        super.render(graphics, mouseX, mouseY, something)
        renderCustomOverlay(graphics, mouseX, mouseY, something)
        if(shouldRenderTooltip()) super.renderTooltip(graphics, mouseX, mouseY)
    }
}