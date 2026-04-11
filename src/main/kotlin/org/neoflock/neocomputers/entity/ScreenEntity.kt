package org.neoflock.neocomputers.entity;

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.network.Networking

class ScreenEntity(blockPos: BlockPos, blockState: BlockState) :
    NodeEntity(BlockEntities.SCREEN_ENTITY.get(), blockPos, blockState) {

    var energyStored: Double = 0.0

    val scrnod = object : Networking.Node() {
        override fun getEnergy() = energyStored
        override fun setEnergy(energy: Double) {
            energyStored = energy
        }
        override fun maxEnergyCapacity(): Double = 10.0
        override fun isConsumer() = true
    }

    // stuff
    override fun getNode() = scrnod
}