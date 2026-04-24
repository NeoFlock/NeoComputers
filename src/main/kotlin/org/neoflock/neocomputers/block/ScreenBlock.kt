package org.neoflock.neocomputers.block;

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.EnumProperty.*
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.ScreenEntity
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.menu.ScreenMenu
import org.neoflock.neocomputers.network.Networking
import kotlin.math.abs
import kotlin.math.max

class ScreenBlock() : NodeBlock() {
    companion object {
        val FACING: EnumProperty<Direction> = EnumProperty.create<Direction>("facing", Direction::class.java)
        val ENERGY: Long = 5
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        val scr = ScreenEntity(blockPos, blockState)
        scr.initNetworking()
        return scr
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide) {
            val screenState = level.getBlockEntity(blockPos, BlockEntities.SCREEN_ENTITY.get()).get()
            if(!screenState.node.consumeEnergy(ENERGY)) {
                player.sendSystemMessage(Component.literal("Not enough power."))
                return InteractionResult.SUCCESS
            };
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos, BlockEntities.SCREEN_ENTITY.get()).get()
            NodeSynchronizer.registerPlayerScreen(sp, ent)
            MenuRegistry.openExtendedMenu(sp, object : ExtendedMenuProvider {
                override fun getDisplayName(): Component = Component.literal("SCREEEEEN!")
                override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
//                    return Menus.SCREEN_MENU.get().create(i, inventory);
                    return ScreenMenu(i, inventory, ent)
                }

                override fun saveExtraData(buf: FriendlyByteBuf?) {
                    buf!!.writeBlockPos(blockPos)
                }
            })
        }
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)!!.setValue(FACING, context.nearestLookingDirection.opposite)
    }
}