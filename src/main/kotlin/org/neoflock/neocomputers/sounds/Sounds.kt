package org.neoflock.neocomputers.sounds

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import org.neoflock.neocomputers.NeoComputers

object Sounds {
    val SOUNDS = DeferredRegister.create(NeoComputers.MODID, Registries.SOUND_EVENT)!!

    val COMPUTER_RUNNING = registerSound("computer_running")

    fun registerSound(name: String) = SOUNDS.register(name) {
        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, name))
    }!!
}