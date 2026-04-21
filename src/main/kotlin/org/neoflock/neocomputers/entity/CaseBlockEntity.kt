package org.neoflock.neocomputers.entity

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.client.sounds.LoopingAudioStream
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
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.block.dirToIdx
import org.neoflock.neocomputers.gui.menu.CaseMenu
import org.neoflock.neocomputers.item.ComponentItem
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import org.neoflock.neocomputers.sounds.ComputerRunningSoundInstance
import org.neoflock.neocomputers.sounds.Sounds
import org.neoflock.neocomputers.utils.GenericContainer
import java.time.Duration
import kotlin.math.min

class CaseBlockEntity(blockPos: BlockPos, blockState: BlockState): NodeBlockEntity(BlockEntities.CASE_ENTITY.get(), blockPos, blockState), MachineEntity, GenericContainer, MenuProvider {
    val stacks: NonNullList<ItemStack> = NonNullList<ItemStack>.withSize(7, ItemStack.EMPTY)

    var isOn = false
    var soundInstance: SoundInstance? = null

    override val node = object : Networking.Node() {
        override var powerRole = PowerRole.STORAGE
        override var energyCapacity: Long = 500
    }

    override fun encodeDownstreamData(packet: FriendlyByteBuf) {
        super.encodeDownstreamData(packet)
        packet.writeBoolean(isOn)
    }

    override fun syncWithUpstream(packet: FriendlyByteBuf) {
        super.syncWithUpstream(packet)
        setRunning(packet.readBoolean())
    }

    override fun processScreenInteraction(player: ServerPlayer, packet: FriendlyByteBuf) {
        val c = packet.readByte().toInt()
        if(c == 0x01) {
            start()
        }
        if(c == 0x02) {
            stop()
        }
    }

    override fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {
        super.encodeScreenData(player, packet)
        packet.writeBoolean(isOn)
    }

    val redstoneIn = Array(Direction.entries.size) {0}
    val redstoneOut = Array(Direction.entries.size) {0}

    fun refetchRedstone(dir: Direction) {
        val src = blockPos.offset(dir.stepX, dir.stepY, dir.stepZ)
        val cur = level?.getSignal(src, dir) ?: 0
        val idx = dirToIdx(dir)
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
    }

    fun onRedstoneSignalChanged(dir: Direction, oldValue: Int, newValue: Int) {
        sendMachineEvent(MachineRedstoneEvent(this, dir, oldValue, newValue))
        Networking.emitMessage(node, Networking.ComputerUncheckedSignal(node, "redstone_changed", arrayOf(node.address.toString(), dirToIdx(dir), oldValue, newValue)))
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
        NeoComputers.LOGGER.info("[${node.address}] Going from $isOn to $value")
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
        world.onBlockStateChange(blockPos, blockState, blockState)
        sendMachineEvent(MachinePowerEvent(this, isOn))
    }

    override fun start(): Boolean {
        setRunning(true)
        return isOn
    }

    override fun stop(): Boolean {
        setRunning(false)
        return isOn
    }

    override fun crash(error: String): Boolean {
        NeoComputers.LOGGER.warn("Crashing cases is not implemented yet lol")
        return false
    }

    override fun getLastError(): String? = null

    override fun getMachineNode(): Networking.Node = node

    override fun getRedstoneInput(direction: Direction): Int = redstoneIn[dirToIdx(direction)]

    override fun getRedstoneOutput(direction: Direction): Int = redstoneOut[dirToIdx(direction)]

    override fun setRedstoneOutput(direction: Direction, newValue: Int): Int {
        val idx = dirToIdx(direction)
        val old = redstoneOut[idx]
        redstoneOut[idx] = newValue
        return old
    }

    override fun beepAsync(frequency: Int, duration: Duration, volume: Double): Boolean {
        NeoComputers.LOGGER.warn("beep not yet implemented")
        return true
    }

    override fun getMachineMemoryTotal(): Long = stacks.mapNotNull { (it.item as? ComponentItem)?.getMemoryCapacity(it) }.sum().toLong()
    override fun getMachineMemoryUsed(): Long = 0
    override fun getMachineComponentsUsed(): Long = node.connections.size.toLong()
    override fun getMachineComponentsTotal(): Long = stacks.mapNotNull { (it.item as? ComponentItem)?.getComponentCapacity(it) }.sum().toLong()

    override fun getItems(): NonNullList<ItemStack> = stacks

    override fun stillValid(player: Player): Boolean = true

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        node.energy = min(node.energyCapacity, compoundTag.getLong("energy"))
        //isOn = compoundTag.getBoolean("powerOn")
        ContainerHelper.loadAllItems(compoundTag, getItems(), provider)
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("energy", node.energy)
        //compoundTag.putBoolean("powerOn", isOn)
        ContainerHelper.saveAllItems(compoundTag, getItems(), provider)
    }

    override fun getDisplayName(): Component? = Component.literal("Computer")
    override fun createMenu(i: Int, inventory: Inventory, player: Player) = CaseMenu(i, inventory, this)

    override fun canPlaceItem(i: Int, itemStack: ItemStack): Boolean = false
    override fun canTakeItem(container: Container, i: Int, itemStack: ItemStack): Boolean = false

    override fun setChanged() {
        super.setChanged()
    }

    override fun setRemoved() {
        setRunning(false)
        super.setRemoved()
    }
}