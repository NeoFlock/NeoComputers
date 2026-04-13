package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.SolarGeneratorBlockEntity
import org.neoflock.neocomputers.entity.CombustionGeneratorBlockEntity

class SolarGeneratorBlock : BaseBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return SolarGeneratorBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is SolarGeneratorBlockEntity) return;
                blockEntity.giveSolarPower();
            }
        }
    }
}

class CombustionGeneratorBlock : BaseBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return CombustionGeneratorBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is CombustionGeneratorBlockEntity) return;
                blockEntity.giveSolarPower();
            }
        }
    }
}
