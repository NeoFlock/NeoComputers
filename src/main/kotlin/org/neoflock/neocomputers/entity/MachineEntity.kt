package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.network.Networking
import java.time.Duration

abstract class MachineEvent {
    abstract val machine: MachineEntity
}

data class MachineRedstoneEvent(override val machine: MachineEntity, val side: Direction, val oldValue: Int, val newValue: Int): MachineEvent()
data class MachinePowerEvent(override val machine: MachineEntity, val nowRunning: Boolean): MachineEvent()

interface MachineEntity {
    // Block position of machine, for wireless tech
    fun getMachineBlockPosition(): BlockPos
    fun getMachineLevel(): Level

    fun beepAsync(frequency: Int, duration: Duration, volume: Double): Boolean

    fun isRunning(): Boolean
    fun start(): Boolean
    fun stop(): Boolean
    fun crash(error: String): Boolean
    fun getLastError(): String?

    fun getMachineNode(): Networking.Node

    // Some metadata
    fun getMachineMemoryTotal(): Long
    fun getMachineMemoryUsed(): Long
    fun getMachineComponentsUsed(): Long
    fun getMachineComponentsTotal(): Long

    // Redstone signals
    fun getRedstoneInput(direction: Direction): Int
    fun getRedstoneOutput(direction: Direction): Int
    // returns the old one
    fun setRedstoneOutput(direction: Direction, newValue: Int): Int
}

// TODO: add dummy machine class which implements the machine entity interface meant to be used on the client