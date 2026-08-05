package org.neoflock.neocomputers.block

import net.minecraft.client.renderer.ItemInHandRenderer
import net.minecraft.core.BlockPos
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.RobotEntity
import org.neoflock.neocomputers.item.Tabs

class RobotBlock : BaseBlock(Properties.of().noOcclusion()), EntityBlock { // todo: node stuff
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        NeoComputers.LOGGER.info("block entity created..")
        return RobotEntity(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape? {
        return Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE // this is so not good
    }

    class RobotBlockItem(block: BaseBlock) : BlockItem(block, Item.Properties().`arch$tab`(Tabs.TAB)) {
    }
}