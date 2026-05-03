package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class RackEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.RACK_ENTITY.get(), pos, state) {
}