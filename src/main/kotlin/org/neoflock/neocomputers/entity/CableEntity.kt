package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.CableBlock
import org.neoflock.neocomputers.block.CableBlock.Companion.COLOR
import org.neoflock.neocomputers.block.CableBlock.Companion.getPropByDirection
import org.neoflock.neocomputers.block.NodeBlockEntity

class CableEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.CABLE_ENTITY.get(), pos, state) {

    override fun setChanged() {
        super.setChanged()
        for (dir in Direction.entries) {
            val ent = level!!.getBlockEntity(blockPos.relative(dir))
            level!!.setBlockAndUpdate(blockPos, blockState.setValue(getPropByDirection(dir), CableBlock.shouldConnect(blockPos, blockPos.relative(dir), level!!)))
        }
    }
}