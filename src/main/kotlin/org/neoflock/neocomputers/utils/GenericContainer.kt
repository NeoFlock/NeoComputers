package org.neoflock.neocomputers.utils

// based off the ImplementedContainer of https://docs.fabricmc.net/develop/blocks/block-containers
import net.minecraft.world.Container;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper
import net.minecraft.world.item.ItemStack

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