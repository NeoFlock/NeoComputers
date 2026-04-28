package org.neoflock.neocomputers.block

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.ComponentUser
import org.neoflock.neocomputers.gui.menu.RelayMenu
import org.neoflock.neocomputers.item.RelayUpgrade
import org.neoflock.neocomputers.network.ConventionalNetworkDevice
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.NodeSynchronizer
import org.neoflock.neocomputers.utils.GenericContainer

class RelayEntity(blockPos: BlockPos, blockState: BlockState): SingleDeviceBlockEntity(BlockEntities.RELAY_ENTITY.get(), blockPos, blockState),
    GenericContainer, ComponentUser, MenuProvider {

    companion object RelaySlots {
        val CARD = 0
        val CPU = 1
        val MEM = 2
        val STORAGE = 3

        val SLOT_COUNT = 4
    }

    val slots = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY)!!

    override fun getItems(): NonNullList<ItemStack> = slots

    override fun stillValid(player: Player): Boolean = true

    fun getUpgrade(slot: Int) = slots[slot].item as? RelayUpgrade

    fun computeRelayInterval(): Int = getUpgrade(CPU)?.getRelayInterval(slots[CPU]) ?: 5
    fun computeRelayBufferSize(): Int = getUpgrade(MEM)?.getRelayBufferSize(slots[MEM]) ?: 1
    fun computeRelayQueueSize(): Int = getUpgrade(STORAGE)?.getRelayQueueSize(slots[STORAGE]) ?: 20
    fun getRelaySender(): DeviceNode? = getUpgrade(CARD)?.getComponentNode(slots[CARD])

    fun computeRelayCapacity(): Int = computeRelayBufferSize() + computeRelayQueueSize()

    val queue = mutableListOf<Networking.ClassicPacket>()
    var active = false
    var ticksUntilQueue = 0

    override val deviceNode = object : DeviceNode() {
        override var reachability = Networking.Visibility.NONE
        override fun received(message: Networking.Message) {
            super.received(message)
            if(message.sender == this) return
            if(message is Networking.ClassicPacket && message.hopCount < 5 && queue.size < computeRelayCapacity()) {
                queue.addLast(message)
            }
        }

        override fun encodeScreenData(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.encodeScreenData(player, buf)
            buf.writeVarInt(computeRelayInterval())
            buf.writeVarInt(computeRelayBufferSize())
            buf.writeVarInt(computeRelayQueueSize())
            buf.writeVarInt(queue.size)
        }

        override fun writeFullStateCommit(buf: FriendlyByteBuf) {
            super.writeFullStateCommit(buf)
            buf.writeBoolean(active)
        }

        override fun processCommit(buf: FriendlyByteBuf) {
            super.processCommit(buf)
            active = buf.readBoolean()
        }
    }

    fun sendQueuedPacket() {
        if(queue.isEmpty()) return
        val pack = queue.removeFirst()

        for(connection in deviceNode.connections) {
            // skip unwanted loopback
            if(connection !in pack.sender.getReachable()) continue
            val hopped = pack.hop(pack.sender)

            if(connection is ConventionalNetworkDevice) {
                connection.sendClassicPacket(hopped)
            } else {
                Networking.emitMessage(connection, hopped, setOf(deviceNode))
            }
        }
    }

    override fun tickDevice(level: Level) {
        super.tickDevice(level)
        if(level !is ServerLevel) return
        ticksUntilQueue--
        if(ticksUntilQueue <= 0) {
            ticksUntilQueue = computeRelayInterval()
            val toSend = computeRelayBufferSize()
            for(i in 0..<toSend) {
                sendQueuedPacket()
            }
        }
        deviceNode.markChanged()
        val cap = computeRelayCapacity()
        while(queue.size > cap) queue.removeLast()
        active = queue.isNotEmpty()
    }

    override fun getMachineBlockPosition() = blockPos!!

    override fun getMachineLevel() = level!!

    override fun getMachineNode() = deviceNode

    override fun getDisplayName() = Component.translatable("block.neocomputers.relay")!!

    override fun createMenu(
        i: Int,
        inventory: Inventory,
        player: Player
    ) = RelayMenu(i, inventory, this)

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        ContainerHelper.loadAllItems(tag, slots, registries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, slots, registries)
    }
}

class RelayBlock: DeviceBlock(Properties.of().sound(SoundType.METAL)) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = RelayEntity(pos, state)

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(pos, BlockEntities.RELAY_ENTITY.get()).get()
            // Open menu
            MenuRegistry.openMenu(player as ServerPlayer, ent)
            NodeSynchronizer.registerPlayerScreen(player, ent.deviceNode)
        }
        return InteractionResult.SUCCESS
    }
}