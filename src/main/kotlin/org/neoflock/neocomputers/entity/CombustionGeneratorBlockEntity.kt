package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity

class CombustionGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : BlockEntity(BlockEntities.COMBUSTGEN_ENTITY.get(), blockPos, blockState) {
    val energyPerTick: Long = 50

    fun giveSolarPower() {
        if(level?.isDay == true) {
            val below = level?.getBlockEntity(blockPos.below())
            if(below is NodeBlockEntity) {
                below.node.giveEnergy(energyPerTick)
            }
        }
    }
}