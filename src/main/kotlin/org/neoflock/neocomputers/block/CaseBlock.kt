package org.neoflock.neocomputers.block;

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.CombustionGeneratorBlock.Companion.COMBUSTGEN_ACTIVE
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.CaseBlockEntity
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.sounds.Sounds

class CaseBlock() : DeviceBlock(Properties.of().sound(SoundType.METAL).lightLevel(CaseBlock::getLuminance)) { // placeholder stuff
    companion object {
        val FACING: EnumProperty<Direction> = EnumProperty.create<Direction>("facing", Direction::class.java)
        val COMPUTER_RUNNING = BooleanProperty.create("running")!!

        fun getLuminance(blockState: BlockState): Int {
            return if(blockState.getValue(COMPUTER_RUNNING)) 3 else 0
        }
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COMPUTER_RUNNING, false))
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState) = CaseBlockEntity(blockPos, blockState)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(COMPUTER_RUNNING)
        builder.add(FACING)
    }

    fun getMachine(level: BlockGetter, blockPos: BlockPos): CaseBlockEntity {
        return level.getBlockEntity(blockPos) as CaseBlockEntity
    }

    override fun isSignalSource(state: BlockState): Boolean = true

    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int {
        return getMachine(blockGetter, blockPos).redstoneOut[direction.opposite.ordinal]
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if(!level.isClientSide) {
            level.updateNeighborsAt(blockPos, this)
            getMachine(level, blockPos).refetchAllRedstone()
        }
        super.onPlace(blockState, level, blockPos, blockState2, bl)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)
        if(!level.isClientSide) {
            val dir = Direction.getNearest(blockPos2.center.subtract(blockPos.center))
            getMachine(level, blockPos).refetchRedstone(dir)
        }
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(blockPos, BlockEntities.CASE_ENTITY.get()).get()
            if(player.isCrouching) {
                // Quickstat
                ent.start()
            } else {
                // Open menu
                MenuRegistry.openMenu(player as ServerPlayer, ent)
                NodeSynchronizer.registerPlayerScreen(player, ent.deviceNode)
            }
        }
        return InteractionResult.SUCCESS
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        Containers.dropContentsOnDestroy(blockState, blockState2, level, blockPos)
        super.onRemove(blockState, level, blockPos, blockState2, bl)
    }
}