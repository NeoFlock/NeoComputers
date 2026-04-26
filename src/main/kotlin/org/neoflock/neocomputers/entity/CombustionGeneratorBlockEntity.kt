package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.CombustionGeneratorBlock
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import org.neoflock.neocomputers.utils.GenericContainer
import org.neoflock.neocomputers.utils.ContainerUtils
import kotlin.math.min

class CombustionGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : NodeBlockEntity(BlockEntities.COMBUSTGEN_ENTITY.get(), blockPos, blockState), GenericContainer, MenuProvider {
    val energyPerTick: Long = 50

    var burningTimeRemaining: Int = 0

    override val deviceNode = object : DeviceNode() {
        override var powerRole = PowerRole.GENERATOR
        override var energyCapacity: Long = 100000
    }

    val stacks: NonNullList<ItemStack> = NonNullList<ItemStack>.withSize(1, ItemStack.EMPTY)

    override fun canPlaceItem(i: Int, itemStack: ItemStack): Boolean {
        return ContainerUtils.isBurningFuel(itemStack)
    }

    override fun getItems(): NonNullList<ItemStack> = stacks

    override fun stillValid(player: Player): Boolean {
        return !this.isRemoved
    }

    override fun tickNode(level: Level) {
        super.tickNode(level)
        // TODO: give us a block state tag for active

        // keep combusting and shi
        if(burningTimeRemaining > 0) {
            burningTimeRemaining--
            deviceNode.giveEnergy(energyPerTick)
            setChanged()
            return
        }

        // no point
        if(deviceNode.energy >= deviceNode.energyCapacity) return;

        // :fire:
        val fuel = stacks[0]
        if(fuel.isEmpty) return

        burningTimeRemaining = ContainerUtils.getBurningTime(fuel) ?: 0
        setChanged()
        fuel.count--
    }

    override fun getDisplayName(): Component? = Component.translatable("block.neocomputers.combustgen")

    override fun createMenu(i: Int, inventory: Inventory, player: Player) = CombustionGeneratorMenu(i, inventory, this)

    override fun setChanged() {
        super.setChanged()
        level?.setBlockAndUpdate(blockPos, blockState.setValue(CombustionGeneratorBlock.COMBUSTGEN_ACTIVE, burningTimeRemaining > 0))
    }

    override fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {
        packet.writeLong(deviceNode.energy)
        packet.writeLong(deviceNode.energyCapacity)
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(compoundTag, provider)
        deviceNode.energy = min(deviceNode.energyCapacity, compoundTag.getLong("energy"))
        burningTimeRemaining = compoundTag.getInt("burningTimeRemaining")
        ContainerHelper.loadAllItems(compoundTag, getItems(), provider)
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("energy", deviceNode.energy)
        compoundTag.putInt("burningTimeRemaining", burningTimeRemaining)
        ContainerHelper.saveAllItems(compoundTag, getItems(), provider)
    }
}