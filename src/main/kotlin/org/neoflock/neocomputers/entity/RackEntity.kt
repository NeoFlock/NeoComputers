package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.DeviceBlockEntity
import org.neoflock.neocomputers.gui.menu.RackMenu
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.utils.GenericContainer

class RackEntity(pos: BlockPos, state: BlockState) : DeviceBlockEntity(BlockEntities.RACK_ENTITY.get(), pos, state), MenuProvider, GenericContainer {
    val stacks: NonNullList<ItemStack> = NonNullList<ItemStack>.withSize(4, ItemStack.EMPTY)

    var conns = mutableListOf(
        -1, -1, -1, -1,
        -1, -1, -1, -1,
        -1, -1, -1, -1,
        -1, -1, -1, -1
    )

    var relayMode = false

    val node: DeviceNode = object : DeviceNode() {
        override var reachability: Networking.Visibility = Networking.Visibility.NONE

        override fun writeFullStateCommit(buf: FriendlyByteBuf) {
            super.writeFullStateCommit(buf)
            buf.writeBoolean(relayMode)


            for (conn in conns) {
                buf.writeInt(conn)
            }
        }

        override fun processCommit(buf: FriendlyByteBuf) {
            super.processCommit(buf)
            relayMode = buf.readBoolean()

            for (i in 0..15) {
                conns[i] = buf.readInt()
            }
        }

        override fun processScreenInteraction(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.processScreenInteraction(player, buf)
            relayMode = buf.readBoolean()

            val slot = buf.readInt()
            NeoComputers.LOGGER.info(slot.toString())
            for (i in 0..3) {
                conns[slot*4+i] = buf.readInt()
                NeoComputers.LOGGER.info("{} {}", slot*4+i, conns[slot*4+i])
            }
            setChanged()
        }

        override fun encodeScreenData(player: ServerPlayer, buf: FriendlyByteBuf) {
            super.encodeScreenData(player, buf)
            buf.writeBoolean(relayMode)

            for (conn in conns) {
                buf.writeInt(conn)
            }
        }
    }

    override fun getDisplayName(): Component? = Component.literal("Rack")

    override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
        return RackMenu(i, inventory, this)
    }

    override fun getDeviceNodes(): List<DeviceNode> = listOf(node)

    override fun getNodeFromSide(directionToRequester: Direction): DeviceNode? = node

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        ContainerHelper.loadAllItems(tag, getItems(), registries)
        relayMode = tag.getBoolean("relay")

        val connarray = tag.getIntArray("conns")
        if (connarray.size == 16) conns = connarray.toMutableList()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        ContainerHelper.saveAllItems(tag, getItems(), registries)
        tag.putBoolean("relay", relayMode)
        tag.putIntArray("conns", conns.toIntArray())
    }

//    override fun getItems(): NonNullList<ItemStack> = items
    override fun getItems(): NonNullList<ItemStack> = stacks

    override fun stillValid(player: Player): Boolean = true
}