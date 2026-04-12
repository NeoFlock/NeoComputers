package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    NodeBlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    override val node = Networking.Node()
}