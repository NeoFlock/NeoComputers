package org.neoflock.neocomputers.entity

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.CaseBlock
import org.neoflock.neocomputers.block.SingleDeviceBlockEntity
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import org.neoflock.neocomputers.sounds.ComputerRunningSoundInstance
import org.neoflock.neocomputers.sounds.Sounds
import org.neoflock.neocomputers.utils.GenericContainer
import java.time.Duration
import kotlin.math.max
import kotlin.math.min
import org.neoflock.neocomputers.network.NodeSynchronizer
import kotlin.text.ifEmpty

class CaseBlockEntity(blockPos: BlockPos, blockState: BlockState): SingleDeviceBlockEntity(BlockEntities.CASE_ENTITY.get(), blockPos, blockState), MachineEntity, GenericContainer, MenuProvider {
    val stacks: NonNullList<ItemStack> = NonNullList<ItemStack>.withSize(7, ItemStack.EMPTY)

    var isOn = false
    var diskActivityTime = 0 // TOOD: writing writers and reading readers
    var networkActivityTime = 0
    var err: String? = null
    var arch = "Lua 5.3"
    var soundInstance: SoundInstance? = null

    override val deviceNode = object : DeviceNode() {
        override var powerRole = PowerRole.CONSUMER
        override var energyCapacity: Long = 500

        override fun writeFullStateCommit(buf: FriendlyByteBuf) {
            super.writeFullStateCommit(buf)
            buf.writeUUID(address)
            buf.writeBoolean(isOn)
            buf.writeVarInt(diskActivityTime)
            buf.writeVarInt(networkActivityTime)
            buf.writeUtf(err ?: "")
        }

        override fun processCommit(buf: FriendlyByteBuf) {
            super.processCommit(buf)
            Networking.changeNodeAddress(this, buf.readUUID())
            setRunning(buf.readBoolean())
            diskActivityTime = buf.readVarInt()
            networkActivityTime = buf.readVarInt()
            err = buf.readUtf().ifEmpty { null }
        }

        override fun processScreenInteraction(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.processScreenInteraction(player, buf)
            val c = buf.readByte().toInt()
            if(c == 0x01) {
                start()
            }
            if(c == 0x02) {
                stop()
            }
        }

        override fun encodeScreenData(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.encodeScreenData(player, buf)
            buf.writeBoolean(isOn)
            buf.writeByteArray((err ?: "").encodeToByteArray())
            buf.writeLong(energy)
            buf.writeLong(energyCapacity)
            buf.writeLong(getMachineMemoryUsed())
            buf.writeLong(getMachineMemoryTotal())
            buf.writeLong(getMachineComponentsUsed())
            buf.writeLong(getMachineComponentsTotal())
            buf.writeUtf(arch)
        }

        override fun tick() {
            super.tick()
            if (isRunning()) {
                if(diskActivityTime > 0) diskActivityTime--
                if(networkActivityTime > 0) networkActivityTime--
                if(getMachineArchitectures().isEmpty()) {
                    crash("@neocomputers.errors.ENOCPU")
                } else if(getMachineComponentsUsed() > getMachineComponentsTotal()) {
                    crash("@neocomputers.errors.E2BIG")
                } else if (!consumeEnergy(1)) {
                    crash("@neocomputers.errors.ENOENJ")
                }
            }
        }
    }

    val redstoneIn = Array(Direction.entries.size) {0}
    val redstoneOut = Array(Direction.entries.size) {0}

    fun refetchRedstone(dir: Direction) {
        val src = blockPos.offset(dir.stepX, dir.stepY, dir.stepZ)
        val cur = level?.getSignal(src, dir) ?: 0
        val idx = dir.ordinal
        if(redstoneIn[idx] != cur) {
            onRedstoneSignalChanged(dir, redstoneIn[idx], cur)
        }
        redstoneIn[idx] = cur
    }

    fun refetchAllRedstone() {
        Direction.entries.forEach { refetchRedstone(it) }
    }

    fun sendMachineEvent(event: MachineEvent) {
        stacks.forEach {
            val item = it.item
            if(item is ComponentItem) {
                item.onMachineEvent(it, this, event)
            }
        }
        Networking.emitMessage(deviceNode, Networking.ComputerEvent(deviceNode, event))
    }

    fun onRedstoneSignalChanged(dir: Direction, oldValue: Int, newValue: Int) {
        sendMachineEvent(MachineRedstoneEvent(this, dir, oldValue, newValue))
        Networking.emitMessage(deviceNode, Networking.ComputerUncheckedSignal(deviceNode, "redstone_changed", arrayOf(deviceNode.address.toString(), dir.ordinal, oldValue, newValue)))
        NeoComputers.LOGGER.info("redstone in direction ${dir.name} changed from $oldValue to $newValue")
        if(oldValue == 0) {
            // Rising edge
            start()
        }
        setChanged()
    }

    override fun getMachineBlockPosition(): BlockPos = blockPos
    override fun getMachineLevel(): Level = level!!

    override fun isRunning(): Boolean = isOn

    fun setRunning(value: Boolean) {
        if(isOn == value) return
        deviceNode.markChanged()
        NeoComputers.LOGGER.info("[${deviceNode.address}] Going from $isOn to $value")
        isOn = value
        val world = level ?: return
        blockState?.setValue(CaseBlock.COMPUTER_RUNNING, isOn)
        if(world.isClientSide) {
            if(value) {
                soundInstance = ComputerRunningSoundInstance(this, Sounds.COMPUTER_RUNNING.get(), SoundSource.AMBIENT)
                Minecraft.getInstance().soundManager.play(soundInstance!!)
            } else {
                Minecraft.getInstance().soundManager.stop(soundInstance!!)
                soundInstance = null
            }
            return
        }
        // Server-side stuff!!
        sendMachineEvent(MachinePowerEvent(this, isOn))
    }

    override fun start(): Boolean {
        if(isOn) return true
        err = null
        val architectures = getMachineArchitectures()
        // Beep patterns taken from https://github.com/MightyPirates/OpenComputers/blob/571482db88080d56329e8f8cf0db2a90825bf1d7/src/main/scala/li/cil/oc/server/machine/Machine.scala
        if(architectures.isEmpty()) {
            crash("@neocomputers.errors.ENOCPU")
            beepAsync("-..")
            return false
        }
        if(getMachineComponentsUsed() > getMachineComponentsTotal()) {
            crash("@neocomputers.errors.E2BIG")
            beepAsync("-..")
            return false
        }
        // less than 20% energy is bad
        if(deviceNode.energy < deviceNode.energyCapacity/5) {
            crash("@neocomputers.errors.ENOENJ")
            // we add a beep for the special case where we do have a little bit of energy :P
            if(deviceNode.energy > 0) beepAsync("..")
            return false
        }
        if(getMachineMemoryTotal() == 0L) {
            crash("@neocomputers.errors.ENOMEM")
            beepAsync("-.")
            return false
        }
        if(arch !in architectures) {
            // Just pick one! TODO: consult EEPROM first
            arch = architectures.first()
        }
        beepAsync(".")
        setRunning(true)
        return isOn
    }

    override fun stop(): Boolean {
        if(!isOn) return false
        setRunning(false)
        return isOn
    }

    override fun crash(error: String): Boolean {
        beepAsync("--")
        sendMachineEvent(MachineCrashEvent(this, error))
        setRunning(false)
        err = error
        return true
    }

    override fun getLastError(): String? = err

    override fun getMachineNode() = deviceNode

    override fun getRedstoneInput(direction: Direction): Int = redstoneIn[direction.ordinal]

    override fun getRedstoneOutput(direction: Direction): Int = redstoneOut[direction.ordinal]

    override fun setRedstoneOutput(direction: Direction, newValue: Int): Int {
        val idx = direction.ordinal
        val old = redstoneOut[idx]
        redstoneOut[idx] = newValue
        return old
    }

    override fun beepAsync(pattern: String, frequency: Int, duration: Duration, volume: Double): Boolean {
        NodeSynchronizer.emitBeep(level!!, NodeSynchronizer.BeepDataPayload(getMachineBlockPosition(), pattern, frequency, duration, volume))
        return true
    }

    override fun signalDiskActivity(delay: Int) {
        diskActivityTime = max(delay, diskActivityTime)
    }

    override fun signalNetworkActivity(delay: Int) {
        networkActivityTime = max(delay, networkActivityTime)
    }

    override fun getMachineMemoryTotal(): Long = stacks.mapNotNull { (it.item as? ComponentItem)?.getMemoryCapacity(it) }.sum().toLong()
    override fun getMachineMemoryUsed(): Long = 0
    override fun getMachineComponentsUsed(): Long = deviceNode.getReachable().size.toLong()
    override fun getMachineComponentsTotal(): Long = stacks.mapNotNull { (it.item as? ComponentItem)?.getComponentCapacity(it) }.sum().toLong()
    override fun getMachineArchitecture() = arch
    override fun getMachineArchitectures() = stacks.mapNotNull { (it.item as? ComponentItem)?.getArchitecturesProvided(it) }.flatten().toSet()
    override fun setMachineArchitecture(arch: String) {
        if(this.arch == arch) return
        this.arch = arch
        if(isRunning()) {
            stop()
            start()
        }
    }

    override fun getItems(): NonNullList<ItemStack> = stacks

    override fun stillValid(player: Player): Boolean = !this.isRemoved

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        deviceNode.energy = min(deviceNode.energyCapacity, compoundTag.getLong("energy"))
        //isOn = compoundTag.getBoolean("powerOn")
        ContainerHelper.loadAllItems(compoundTag, getItems(), provider)
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("energy", deviceNode.energy)
        //compoundTag.putBoolean("powerOn", isOn)
        ContainerHelper.saveAllItems(compoundTag, getItems(), provider)
    }

    override fun getDisplayName(): Component? = Component.literal("Computer")
    override fun createMenu(i: Int, inventory: Inventory, player: Player) = CaseMenu(i, inventory, this)

    override fun setRemoved() {
        setRunning(false)
        super.setRemoved()
    }

    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = false
    override fun canTakeItem(target: Container, slot: Int, stack: ItemStack): Boolean = false
}