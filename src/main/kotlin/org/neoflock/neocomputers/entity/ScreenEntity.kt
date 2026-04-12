package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    NodeBlockEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    var energyStored: Double = 0.0

    override val node = object : Networking.Node() {
        override fun getPowerRole() = PowerRole.CONSUMER
        override fun getEnergy() = energyStored
        override fun setEnergy(energy: Double) {
            energyStored = energy
        }
        override fun maxEnergyCapacity(): Double = 10.0
    }
}