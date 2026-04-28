package org.neoflock.neocomputers.block

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.SolarGeneratorBlockEntity
import org.neoflock.neocomputers.entity.CombustionGeneratorBlockEntity
import org.neoflock.neocomputers.network.NodeSynchronizer

class SolarGeneratorBlock : DeviceBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = SolarGeneratorBlockEntity(blockPos, blockState)
}

// TODO: make it glow when burning
class CombustionGeneratorBlock : DeviceBlock, EntityBlock {
    companion object {
        val COMBUSTGEN_ACTIVE = BooleanProperty.create("active")

        fun getLuminance(blockState: BlockState): Int {
            return if(blockState.getValue(COMBUSTGEN_ACTIVE)) 5 else 0
        }
    }

    constructor(): super(Properties.of().sound(SoundType.STONE).lightLevel(CombustionGeneratorBlock::getLuminance)) {
        registerDefaultState(defaultBlockState().setValue(COMBUSTGEN_ACTIVE, false))
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = CombustionGeneratorBlockEntity(blockPos, blockState)

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide()) {
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos, BlockEntities.COMBUSTGEN_ENTITY.get()).get()
            NodeSynchronizer.registerPlayerScreen(sp, ent.deviceNode)
            MenuRegistry.openMenu(sp, ent)
        }
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(COMBUSTGEN_ACTIVE)
    }

    override fun animateTick(blockState: BlockState, level: Level, blockPos: BlockPos, randomSource: RandomSource) {
        if(blockState.getValue(COMBUSTGEN_ACTIVE)) {
            if(randomSource.nextDouble() < 0.1) level.playLocalSound(blockPos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.AMBIENT, 1F, 1F, false)

            val x = blockPos.x.toDouble()
            val y = blockPos.y.toDouble()
            val z = blockPos.z.toDouble()

            level.addParticle(ParticleTypes.SMOKE, x+0.5, y+1, z+0.5, 0.0, 0.0, 0.0)
            level.addParticle(ParticleTypes.FLAME, x+0.5, y+1, z+0.5, 0.0, 0.0, 0.0)
        }
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
