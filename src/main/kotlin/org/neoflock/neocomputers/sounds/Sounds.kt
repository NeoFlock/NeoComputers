package org.neoflock.neocomputers.sounds

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.phys.Vec3
import org.lwjgl.BufferUtils
import org.neoflock.neocomputers.NeoComputers
import org.lwjgl.openal.AL10
import java.nio.ByteBuffer
import kotlin.experimental.xor
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.sign
import kotlin.math.sin

object Sounds {
    val SOUNDS = DeferredRegister.create(NeoComputers.MODID, Registries.SOUND_EVENT)!!

    val COMPUTER_RUNNING = registerSound("computer_running")

    fun registerSound(name: String) = SOUNDS.register(name) {
        SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, name))
    }!!

    val BEEP_SAMPLERATE = 44100
    val BEEP_AMPLITUDE = 32f
    val BEEP_MAXDIST = 16f

    // Also largely taken from https://github.com/MightyPirates/OpenComputers/blob/571482db88080d56329e8f8cf0db2a90825bf1d7/src/main/scala/li/cil/oc/util/Audio.scala

    val allSounds = ThreadLocal.withInitial { mutableListOf<CustomSoundBuffer>() }

    class CustomSoundBuffer {
        var dead: Boolean = true
        var buffer: Int = -1
        var source: Int = -1

        fun start(x: Float, y: Float, z: Float, data: ByteBuffer, gain: Float): Int? {
            // clear errors or smth idk
            AL10.alGetError()

            // written in a C style by a C dev
            // all this work on a JVM project and I'm still writing C
            // would be better if Kotlin had goto btw just saying
            val ok = AL10.AL_NO_ERROR
            var err = ok
            buffer = AL10.alGenBuffers()
            err = AL10.alGetError()
            if(err != ok) return err

            AL10.alBufferData(buffer, AL10.AL_FORMAT_MONO8, data, BEEP_SAMPLERATE)
            err = AL10.alGetError()
            if(err != ok) {
                AL10.alDeleteBuffers(buffer)
                return err
            }

            source = AL10.alGenSources()
            err = AL10.alGetError()
            if(err != ok) {
                AL10.alDeleteBuffers(buffer)
                return err
            }

            AL10.alSourceQueueBuffers(source, buffer)
            err = AL10.alGetError()
            if(err != ok) {
                AL10.alDeleteBuffers(buffer)
                AL10.alDeleteSources(source)
                return err
            }

            AL10.alSource3f(source, AL10.AL_POSITION, x, y, z)
            AL10.alSourcef(source, AL10.AL_REFERENCE_DISTANCE, BEEP_MAXDIST)
            AL10.alSourcef(source, AL10.AL_MAX_DISTANCE, BEEP_MAXDIST)
            AL10.alSourcef(source, AL10.AL_GAIN, gain * 0.3f)
            err = AL10.alGetError()
            if(err != ok) {
                AL10.alDeleteBuffers(buffer)
                AL10.alDeleteSources(source)
                return err
            }

            AL10.alSourcePlay(source)
            err = AL10.alGetError()
            if(err != ok) {
                AL10.alDeleteBuffers(buffer)
                AL10.alDeleteSources(source)
                return err
            }

            dead = false
            return null
        }

        fun checkDone(): Boolean {
            if(dead) return true
            if(AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) return false
            NeoComputers.LOGGER.info("sound buffer stopped")
            dead = true
            AL10.alDeleteSources(source)
            AL10.alDeleteBuffers(buffer)
            return true
        }
    }

    fun beep(pos: Vec3, pattern: String, frequency: Int = 1000, duration: Int = 200) {
        NeoComputers.LOGGER.info("Beep: $pattern, $frequency Hz, $duration ms")
        val mc = Minecraft.getInstance()
        val playerPos = mc.player?.position() ?: pos
        val distanceBasedGain = max(0.0, 1 - pos.distanceTo(playerPos) / BEEP_MAXDIST).toFloat()
        val volume = 1.0
        val gain = distanceBasedGain * volume
        if (gain <= 0 || BEEP_AMPLITUDE <= 0) return

        // Algorithm effectively ported over from https://github.com/MightyPirates/OpenComputers/blob/571482db88080d56329e8f8cf0db2a90825bf1d7/src/main/scala/li/cil/oc/util/Audio.scala
        // We do add support for spaces tho
        val charArr = pattern.toCharArray()
        val sampleCounts = charArr.map { if(it == '.') duration else 2 * duration }.map { it * BEEP_SAMPLERATE / 1000 }
        val pauseSample = 50 * BEEP_SAMPLERATE / 1000

        val finalBuf = BufferUtils.createByteBuffer(sampleCounts.sum() + pauseSample * sampleCounts.lastIndex)
        val step = frequency.toFloat() / BEEP_SAMPLERATE
        var off = 0f
        for((i, sampleCount) in sampleCounts.withIndex()) {
            if(charArr[i] == ' ') {
                for(sample in 0..<sampleCount) {
                    finalBuf.put(127)
                }
            } else {
                for(sample in 0..<sampleCount) {
                    val angle = 2 * PI * off
                    val value = (sin(angle).sign * BEEP_AMPLITUDE).toInt().toByte().xor(0x80.toByte())
                    off += step
                    if(off > 1) off -= 1f
                    finalBuf.put(value)
                }
            }
            if(finalBuf.hasRemaining()) {
                for(sample in 0..<pauseSample) {
                    finalBuf.put(127)
                }
            }
        }
        finalBuf.rewind()
        val l = mutableListOf<Int>()
        while(finalBuf.hasRemaining()) l.addLast(finalBuf.get().toInt())
        finalBuf.rewind()
        NeoComputers.LOGGER.info("$l")

        val sound = CustomSoundBuffer()
        val soundErr = sound.start(pos.x.toFloat(), pos.y.toFloat(), pos.z.toFloat(), finalBuf, gain.toFloat())
        if(soundErr != null) {
            NeoComputers.LOGGER.error("Playing beep failed, OpenAL exit code of $soundErr")
            return
        }

        NeoComputers.LOGGER.info("Beeping with ${finalBuf.capacity()} samples")
        allSounds.get().addLast(sound)
    }

    fun tickCustomSounds() {
        allSounds.get().removeIf { it.checkDone() }
    }
}