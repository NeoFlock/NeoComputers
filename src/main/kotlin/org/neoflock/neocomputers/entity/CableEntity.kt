package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.CableBlock
import org.neoflock.neocomputers.block.NodeBlockEntity

class CableEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.CABLE_ENTITY.get(), pos, state) {

    override fun setChanged() {
        super.setChanged()
//        val neighbors = getNeighbourEntities()

        for (dir in Direction.entries) {
            val ent = level!!.getBlockEntity(blockPos.relative(dir))
            level!!.setBlockAndUpdate(blockPos, blockState.setValue(CableBlock.getPropByDirection(dir), (ent is NodeBlockEntity || ent is CableEntity)))
        }

    }
//    fun getNeighbourEntities(): List<BlockEntity> {
//        val subpos = listOf(
//            blockPos.offset(0, 0, 1),
//            blockPos.offset(0, 0, -1),
//            blockPos.offset(0, 1, 0),
//            blockPos.offset(0, -1, 0),
//            blockPos.offset(1, 0, 0),
//            blockPos.offset(-1, 0, 0),
//        )
//
//        return subpos.mapNotNull { pos -> level?.getBlockEntity(pos) }
//    }
}