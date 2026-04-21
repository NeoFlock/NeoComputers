package org.neoflock.neocomputers.sounds

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import org.neoflock.neocomputers.entity.MachineEntity

class ComputerRunningSoundInstance: AbstractTickableSoundInstance {
    val machine: MachineEntity

    fun updatePosition() {
        val pos = machine.getBlockPosition()
        this.x = pos.x.toDouble() + 0.5
        this.y = pos.y.toDouble() + 0.5
        this.z = pos.z.toDouble() + 0.5
    }

    constructor(machine: MachineEntity, soundEvent: SoundEvent, soundSource: SoundSource): super(soundEvent, soundSource, SoundInstance.createUnseededRandom()) {
        this.machine = machine
        this.looping = true
        this.delay = 0
        this.volume = 1.0F
        this.relative = true
        updatePosition()
    }

    override fun tick() {
        if(!machine.isRunning()) {
            this.stop()
        } else {
            updatePosition()
        }
    }
}