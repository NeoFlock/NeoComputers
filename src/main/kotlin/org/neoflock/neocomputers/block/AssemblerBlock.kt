package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.entity.AssemblerEntity

class AssemblerBlock : BaseBlock(), EntityBlock { // TODO: component stuff
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return AssemblerEntity(pos, state)
    }
}