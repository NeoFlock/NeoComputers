package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    BlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    // stuff
}