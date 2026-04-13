package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import kotlin.math.min

class SolarGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : NodeBlockEntity(BlockEntities.SOLARGEN_ENTITY.get(), blockPos, blockState) {
    val energyPerTick: Long = 50
    var energyStored: Long = 0
    val capacity: Long = 50000

    override val node = object : Networking.Node() {
        override fun getPowerRole(): PowerRole = PowerRole.GENERATOR
        override fun getEnergy(): Long = energyStored
        override fun getEnergyCapacity(): Long = capacity
        override fun giveEnergy(amount: Long): Long {
            val taken = min(amount, capacity - energyStored)
            energyStored += taken
            return taken
        }
        override fun withdrawEnergy(amount: Long): Long {
            val taken = min(amount, energyStored)
            energyStored -= taken
            return taken
        }
    }

    override fun tickNode() {
        super.tickNode()
        val l = level ?: return
        if(l.isDay) {
            node.giveEnergy(energyPerTick)
        }
    }
}