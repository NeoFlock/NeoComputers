package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.CombustionGeneratorBlock
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import org.neoflock.neocomputers.utils.GenericContainer
import org.neoflock.neocomputers.utils.ContainerUtils
import kotlin.math.min

class CombustionGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : NodeBlockEntity(BlockEntities.COMBUSTGEN_ENTITY.get(), blockPos, blockState), GenericContainer, MenuProvider {
    val energyPerTick: Long = 50

    var burningTimeRemaining: Int = 0

    override val node = object : Networking.Node() {
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
            node.giveEnergy(energyPerTick)
            setChanged()
            return
        }

        // no point
        if(node.energy >= node.energyCapacity) return;

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
        level?.setBlockAndUpdate(blockPos, blockState.setValue(CombustionGeneratorBlock.ACTIVE, burningTimeRemaining > 0))
    }

    override fun encodeScreenData(player: ServerPlayer, packet: FriendlyByteBuf) {
        packet.writeLong(node.energy)
        packet.writeLong(node.energyCapacity)
    }

    override fun loadAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        node.energy = min(node.energyCapacity, compoundTag.getLong("energy"))
        burningTimeRemaining = compoundTag.getInt("burningTimeRemaining")
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        compoundTag.putLong("energy", node.energy)
        compoundTag.putInt("burningTimeRemaining", burningTimeRemaining)
    }
}