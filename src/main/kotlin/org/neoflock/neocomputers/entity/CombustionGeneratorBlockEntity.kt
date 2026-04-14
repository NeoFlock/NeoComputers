package org.neoflock.neocomputers.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import org.neoflock.neocomputers.block.NodeBlockEntity
import org.neoflock.neocomputers.gui.menu.CombustionGeneratorMenu
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import org.neoflock.neocomputers.utils.GenericContainer
import org.neoflock.neocomputers.utils.ContainerUtils
import kotlin.math.min

class CombustionGeneratorBlockEntity(blockPos: BlockPos, blockState: BlockState) : NodeBlockEntity(BlockEntities.COMBUSTGEN_ENTITY.get(), blockPos, blockState), GenericContainer, MenuProvider {
    val energyPerTick: Long = 50

    var energy: Long = 0
    val maxEnergy: Long = 100000
    var burningTimeRemaining: Int = 0

    override val node = object : Networking.Node() {
        override fun getPowerRole() = PowerRole.GENERATOR
        override fun getEnergy() = energy
        override fun getEnergyCapacity() = maxEnergy
        override fun withdrawEnergy(amount: Long): Long {
            val taken = min(amount, energy)
            energy -= taken
            return taken
        }

        override fun giveEnergy(amount: Long): Long {
            val given = min(amount, maxEnergy - energy)
            energy += given
            return given
        }
    }

    val stacks: NonNullList<ItemStack> = NonNullList<ItemStack>.withSize(1, ItemStack.EMPTY)

    override fun canPlaceItem(i: Int, itemStack: ItemStack): Boolean {
        return ContainerUtils.isBurningFuel(itemStack)
    }

    override fun getItems(): NonNullList<ItemStack> = stacks

    override fun stillValid(player: Player): Boolean {
        return !this.isRemoved
    }

    override fun tickNode() {
        super.tickNode()
        // TODO: give us a block state tag for active

        // keep combusting and shi
        if(burningTimeRemaining > 0) {
            burningTimeRemaining--
            node.giveEnergy(energyPerTick)
            setChanged()
            return
        }

        // no point
        if(node.getEnergy() >= node.getEnergyCapacity()) return;

        // :fire:
        val fuel = stacks[0]
        if(fuel.isEmpty) return

        burningTimeRemaining = ContainerUtils.getBurningTime(fuel) ?: 0
        fuel.count--
    }

    override fun getDisplayName(): Component? = Component.translatable("block.neocomputers.combustgen")

    override fun createMenu(i: Int, inventory: Inventory, player: Player) = CombustionGeneratorMenu(i, inventory, this)
}