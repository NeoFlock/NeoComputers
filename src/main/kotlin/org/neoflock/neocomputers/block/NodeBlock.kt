package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.redstone.Orientation
import org.neoflock.neocomputers.network.Networking

abstract class NodeBlockEntity(blockEntityType: BlockEntityType<*>, blockPos: BlockPos, blockState: BlockState) : BlockEntity(blockEntityType, blockPos, blockState) {
    abstract val node: Networking.Node

    fun initNetworking() {
        Networking.addNode(node)
    }

    private var stateIsDirty = true

    open fun getNeighbourEntities(): List<BlockEntity> {
        val subpos = listOf(
            blockPos.offset(0, 0, 1),
            blockPos.offset(0, 0, -1),
            blockPos.offset(0, 1, 0),
            blockPos.offset(0, -1, 0),
            blockPos.offset(1, 0, 0),
            blockPos.offset(-1, 0, 0),
        )

        return subpos.mapNotNull { pos -> level?.getBlockEntity(pos) }
    }

    fun computeEdges(): Set<NodeBlockEntity> {
        val s = mutableSetOf<NodeBlockEntity>()
        val neighbours = getNeighbourEntities()
        for(neighbour in neighbours) {
            if(neighbour is NodeBlockEntity) s.add(neighbour);
            // TODO: handle cable entities
        }
        s.remove(this)
        return s
    }

    fun invalidateNodeState() {
        stateIsDirty = true
    }

    fun needsSynchronization() = stateIsDirty

    fun ensureSynchronized() {
        if(!stateIsDirty) return
        stateIsDirty = false
        computeEdges().forEach {
            node.connectTo(it.node)
        }
    }

    override fun setChanged() {
        invalidateNodeState()
        computeEdges().forEach { it.invalidateNodeState() }
        super.setChanged()
    }

    override fun setRemoved() {
        super.setRemoved()
        Networking.removeNode(node)
    }
}

abstract class NodeBlock(name: String): BaseBlock(name), EntityBlock {
    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is NodeBlockEntity) return;
                blockEntity.ensureSynchronized()
            }
        }
    }

    override fun setPlacedBy(
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        livingEntity: LivingEntity?,
        itemStack: ItemStack
    ) {
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(blockPos)
            if(ent is NodeBlockEntity) {
                ent.invalidateNodeState()
            }
        }
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack)
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        orientation: Orientation?,
        bl: Boolean
    ) {
        if(!level.isClientSide) {
            val ent = level.getBlockEntity(blockPos)
            if(ent is NodeBlockEntity) {
                ent.invalidateNodeState()
            }

        }
        super.neighborChanged(blockState, level, blockPos, block, orientation, bl)
    }
}