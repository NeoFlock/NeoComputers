package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.network.Networking

abstract class MachineEvent {
    abstract val machine: MachineEntity
}

data class MachineRedstoneEvent(override val machine: MachineEntity, val side: Direction): MachineEvent()

interface MachineEntity {
    // Block position of machine, for wireless tech
    fun getBlockPosition(): BlockPos

    fun isRunning(): Boolean
    fun start(): Boolean
    fun stop(): Boolean
    fun crash(error: String): Boolean

    fun getMachineNode(): Networking.Node

    // Redstone signals
    fun getRedstoneInput(direction: Direction): Int
    fun getRedstoneOutput(direction: Direction): Int
    // returns the old one
    fun setRedstoneOutput(direction: Direction, newValue: Int): Int
}

// TODO: add dummy machine class which implements the machine entity interface meant to be used on the client