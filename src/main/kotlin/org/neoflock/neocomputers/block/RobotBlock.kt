package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.RobotEntity

class RobotBlock : BaseBlock(Properties.of().noOcclusion()), EntityBlock { // todo: node stuff
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        NeoComputers.LOGGER.info("block entity created..")
        Blocks.CHEST
        return RobotEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE // this is so not good
    }
//    public RenderShape getRenderShape(BlockState state) {
//        return RenderShape.ENTITYBLOCK_ANIMATED;
//    }
}