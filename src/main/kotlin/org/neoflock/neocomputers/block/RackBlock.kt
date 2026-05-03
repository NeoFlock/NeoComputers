package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.RackEntity

class RackBlock : BaseBlock(Properties.of().noOcclusion()), EntityBlock {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return RackEntity(pos, state)
    }

//    override fun getShape(
//        state: BlockState,
//        level: BlockGetter,
//        pos: BlockPos,
//        context: CollisionContext
//    ): VoxelShape? {
//        return Shapes.box(0.0,0.0,0.0,0.01,0.01,0.01)
//    }

//    override fun getRenderShape(state: BlockState): RenderShape? {
//        return RenderShape
//    }


//    override fun useWithoutItem(
//        state: BlockState,
//        level: Levesl,
//        pos: BlockPos,
//        player: Player,
//        hitResult: BlockHitResult
//    ): InteractionResult? {
//        return super.useWithoutItem(state, level, pos, player, hitResult)
}