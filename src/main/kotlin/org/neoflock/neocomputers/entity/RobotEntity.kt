package org.neoflock.neocomputers.entity

import net.minecraft.client.model.geom.ModelPart
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers

class RobotEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockEntities.ROBOT_ENTITY.get(), pos, state,) {
    val body: ModelPart? = null
    val name = "Diddyx" //TODO: names

    init {
        NeoComputers.LOGGER.info("yooo")
    }
}