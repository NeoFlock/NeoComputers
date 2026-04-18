package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole

class SolarGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : NodeBlockEntity(BlockEntities.SOLARGEN_ENTITY.get(), blockPos, blockState) {
    val energyPerTick: Long = 50

    override val node = object : Networking.Node() {
        override var powerRole: PowerRole = PowerRole.GENERATOR
        override var energyCapacity: Long = 50000
    }

    override fun tickNode(level: Level) {
        super.tickNode(level)
        val l = level
        if(l.isDay) {
            node.giveEnergy(energyPerTick)
        }
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        node.energy = compoundTag.getLong("energy")
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        compoundTag.putLong("energy", node.energy)
    }
}