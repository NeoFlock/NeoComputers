package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class AssemblerEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.ASSEMBLER_ENTITY.get(), pos, state) {
}