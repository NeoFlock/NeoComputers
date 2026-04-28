package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.SingleDeviceBlockEntity
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.PowerRole

class SolarGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : SingleDeviceBlockEntity(BlockEntities.SOLARGEN_ENTITY.get(), blockPos, blockState) {
    val energyPerTick: Long = 50

    override val deviceNode = object : DeviceNode() {
        override var powerRole: PowerRole = PowerRole.GENERATOR
        override var energyCapacity: Long = 50000
    }

    override fun tickDevice(level: Level) {
        super.tickDevice(level)
        val l = level
        if(l.isDay) {
            deviceNode.giveEnergy(energyPerTick)
        }
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        deviceNode.energy = compoundTag.getLong("energy")
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("energy", deviceNode.energy)
    }
}