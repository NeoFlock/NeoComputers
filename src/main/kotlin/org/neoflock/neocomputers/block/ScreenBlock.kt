package org.neoflock.neocomputers.block;

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
            if(!screenState.node.consumeEnergy(5)) {
                player.sendSystemMessage(Component.literal("Not enough power."))
                return InteractionResult.SUCCESS
            };
            MenuRegistry.openMenu(player as ServerPlayer, object : MenuProvider {
                override fun getDisplayName(): Component = Component.literal("SCREEEEEN!")
                override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
                    return Menus.SCREEN_MENU.get().create(i, inventory);
                }
            })
        }
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }
    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
//        state.setValue(state.)
        level.setBlockAndUpdate(pos, state.setValue(FACING, lookingdir(placer!!)))
    }

    private fun lookingdir(placer: LivingEntity): Direction {
        val vec = placer.lookAngle
        NeoComputers.LOGGER.info(vec.toString())
        val biggest = max(max(abs(vec.x), abs(vec.y)), abs(vec.z))
        when(biggest) {
            abs(vec.x) -> if(vec.x < 0) return Direction.EAST else return Direction.WEST
            abs(vec.y) -> if(vec.y < 0) return Direction.UP else return Direction.DOWN
            abs(vec.z) -> if(vec.z < 0) return Direction.SOUTH else return Direction.NORTH
        }
        NeoComputers.LOGGER.warn("Failed to obtain looking direction!")
        return Direction.NORTH // wtf
    }
}