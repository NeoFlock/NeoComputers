package org.neoflock.neocomputers.block;

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.ScreenEntity
import org.neoflock.neocomputers.gui.menu.Menus

class ScreenBlock() : BaseBlock("screen"), EntityBlock {

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        return ScreenEntity(blockPos, blockState)
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide) {
            MenuRegistry.openMenu(player as ServerPlayer, object : MenuProvider {
                override fun getDisplayName(): Component = Component.literal("SCREEEEEN!")
                override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
                    return Menus.SCREEN_MENU.get().create(i, inventory);
                }
            })
        }
        return InteractionResult.SUCCESS
    }
}