package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.CableBlock
import org.neoflock.neocomputers.block.CableBlock.Companion.COLOR
import org.neoflock.neocomputers.block.CableBlock.Companion.getPropByDirection
import org.neoflock.neocomputers.block.DeviceBlockEntity
import org.neoflock.neocomputers.block.SingleDeviceBlockEntity
import org.neoflock.neocomputers.network.DeviceNode

class CableEntity(pos: BlockPos, state: BlockState) : SingleDeviceBlockEntity(BlockEntities.CABLE_ENTITY.get(), pos, state) {
    override val deviceNode = object : DeviceNode(){}

    override fun sendCommitsToClient(level: Level) {
        // we have nothing to commit lol
        return
    }

    override fun requestServerState() {
        // no state, we don't bother
        return
    }

    override fun getNodeFromSide(directionToRequester: Direction): DeviceNode? {
        if(CableBlock.shouldConnect(blockPos, blockPos.relative(directionToRequester), level!!)) {
            return deviceNode
        }
        return null
    }

    override fun setChanged() {
        super.setChanged()
        for (dir in Direction.entries) {
            val ent = level!!.getBlockEntity(blockPos.relative(dir))
            level!!.setBlockAndUpdate(blockPos, blockState.setValue(getPropByDirection(dir), CableBlock.shouldConnect(blockPos, blockPos.relative(dir), level!!)))
            if(ent is DeviceBlockEntity) {
                ent.connectionsAreDirty = true
            }
        }
    }
}