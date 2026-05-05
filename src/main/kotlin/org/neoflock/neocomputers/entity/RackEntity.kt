package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.gui.menu.RackMenu

class RackEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.RACK_ENTITY.get(), pos, state), MenuProvider {
    override fun getDisplayName(): Component? = Component.literal("Rack")

    override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RackMenu(i, inventory)
    }
}