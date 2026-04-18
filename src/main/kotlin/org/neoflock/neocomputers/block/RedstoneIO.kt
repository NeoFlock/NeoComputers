package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RedStoneWireBlock
import net.minecraft.world.level.block.RedstoneTorchBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.network.Networking

fun dirToIdx(direction: Direction) = Direction.entries.indexOf(direction)

class RedstoneIOEntity(blockPos: BlockPos, blockState: BlockState): NodeBlockEntity(BlockEntities.REDSTONEIO_ENTITY.get(), blockPos, blockState) {
    val redstoneIn = Array<Int>(Direction.entries.size) {0}
    val redstoneOut = Array<Int>(Direction.entries.size) {0}

    // TODO: have redstone I/O node for component and shi
    override val node = object : Networking.Node() {

    }

    fun refetch(dir: Direction) {
        val src = blockPos.offset(dir.stepX, dir.stepY, dir.stepZ)
        val cur = level?.getSignal(src, dir) ?: 0
        val idx = dirToIdx(dir)
        if(redstoneIn[idx] != cur) {
            onRedstoneSignalChanged(dir, redstoneIn[idx], cur)
        }
        redstoneIn[idx] = cur
    }

    fun refetchAll() {
        Direction.entries.forEach { refetch(it) }
    }

    fun onRedstoneSignalChanged(dir: Direction, oldValue: Int, newValue: Int) {
        Networking.emitMessage(node, Networking.ComputerUncheckedSignal(node, "redstone_changed", arrayOf(node.address.toString(), dirToIdx(dir), oldValue, newValue)))
        NeoComputers.LOGGER.info("redstone in direction ${dir.name} changed from $oldValue to $newValue")
    }
}

class RedstoneIOBlock(): NodeBlock(Properties.of().isRedstoneConductor { state, getter, pos -> true }) {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = RedstoneIOEntity(blockPos, blockState)

    fun getRedstoneIO(level: BlockGetter, blockPos: BlockPos): RedstoneIOEntity? {
        val ent = level.getBlockEntity(blockPos)
        if(ent is RedstoneIOEntity) return ent
        return null
    }

    override fun isSignalSource(blockState: BlockState): Boolean {
        return true
    }

    override fun getSignal(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        direction: Direction
    ): Int {
        val redstoneIO = getRedstoneIO(blockGetter, blockPos)
        if(redstoneIO != null) {
            return redstoneIO.redstoneOut[dirToIdx(direction.opposite)]
        }
        return super.getSignal(blockState, blockGetter, blockPos, direction)
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if(!level.isClientSide) {
            level.updateNeighborsAt(blockPos, this)
            getRedstoneIO(level, blockPos)?.refetchAll()
        }
        super.onPlace(blockState, level, blockPos, blockState2, bl)
    }

    override fun onRemove(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        if(!level.isClientSide) {
            level.updateNeighborsAt(blockPos, this)
        }
        super.onRemove(blockState, level, blockPos, blockState2, bl)
    }

    override fun neighborChanged(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        block: Block,
        blockPos2: BlockPos,
        bl: Boolean
    ) {
        super.neighborChanged(blockState, level, blockPos, block, blockPos2, bl)
        if(!level.isClientSide) {
            val dir = Direction.getNearest(blockPos2.center.subtract(blockPos.center))
            getRedstoneIO(level, blockPos)?.refetch(dir)
        }
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult? {
        if(!level.isClientSide) {
            val redio = getRedstoneIO(level, blockPos)
            val dir = blockHitResult.direction
            if (redio != null) {
                val idx = dirToIdx(dir)
                redio.redstoneOut[idx]++
                redio.redstoneOut[idx] %= 16
                NeoComputers.LOGGER.info("outputting redstone level ${redio.redstoneOut[idx]} on ${dir.name}")
            }
            level.updateNeighborsAt(blockPos, this)
        }
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult)
    }
}