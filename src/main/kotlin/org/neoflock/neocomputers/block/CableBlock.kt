package org.neoflock.neocomputers.block

import net.minecraft.client.renderer.blockentity.PistonHeadRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.neoflock.neocomputers.entity.CableEntity

class CableBlock() : BaseBlock(Properties.of()), EntityBlock {
    companion object {
        val NORTH = BooleanProperty.create("north")
        val EAST = BooleanProperty.create("east")
        val WEST = BooleanProperty.create("west")
        val SOUTH = BooleanProperty.create("south")
        val UP = BooleanProperty.create("up")
        val DOWN = BooleanProperty.create("down")

        val MIN = 0.375
        val MAX = 1-MIN

//        val shapeCache: Array<VoxelShape?> = arrayOfNulls(Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size)
//
//        fun makeShapes() { // screw perf
//            for (north in arrayOf(false, true)) { // shut up
//                for (east in arrayOf(false, true)) {
//                    for (west in arrayOf(false, true)) {
//                        for (south in arrayOf(false, true)) {
//                            for (up in arrayOf(false, true)) {
//                                for (down in arrayOf(false, true)) {
//                                    val shape = makeShape(north, south, east, west, up, down)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        fun makeShape(north: Boolean, south: Boolean, east: Boolean, west: Boolean, up: Boolean, down: Boolean): VoxelShape {
            var shape = Shapes.box(MIN, MIN, MIN, MAX, MAX, MAX)
            if (north) shape = Shapes.join(shape, Shapes.box(MIN, MIN, 0.0, MAX, MAX, MIN ), BooleanOp.OR)
            if (south) shape = Shapes.join(shape, Shapes.box(MIN, MIN, MAX, MAX, MAX, 1.0 ), BooleanOp.OR)
            if (east) shape = Shapes.join(shape, Shapes.box(MAX, MIN, MIN, 1.0, MAX, MAX), BooleanOp.OR)
            if (west) shape = Shapes.join(shape, Shapes.box(0.0, MIN, MIN, MIN, MAX, MAX ), BooleanOp.OR)
            if (up) shape = Shapes.join(shape, Shapes.box(MIN, MAX, MIN, MAX, 1.0, MAX), BooleanOp.OR)
            if (down) shape = Shapes.join(shape, Shapes.box(MIN, 0.0, MIN, MAX, MIN, MAX ), BooleanOp.OR)
            return shape
        }

        fun getPropByDirection(direction: Direction): BooleanProperty {
            return when (direction) {
                Direction.NORTH -> NORTH
                Direction.SOUTH -> SOUTH
                Direction.WEST -> WEST
                Direction.EAST -> EAST
                Direction.UP -> UP
                Direction.DOWN -> DOWN
            }
        }
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(WEST, false)
            .setValue(SOUTH, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        )
    }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return CableEntity(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape? {
        return makeShape(state.getValue(NORTH), state.getValue(SOUTH), state.getValue(EAST), state.getValue(WEST), state.getValue(UP), state.getValue(DOWN))
    }

    override fun neighborChanged(state: BlockState, level: Level, pos: BlockPos, neighborBlock: Block, neighborPos: BlockPos, movedByPiston: Boolean) {
//        val neighbors = getNeighbourEntities(blockPos, level)
//        for (dir in Direction.entries) {
//            val ent = level.getBlockEntity(blockPos.relative(dir))
//            level.setBlockAndUpdate(blockPos, blockState.setValue(getPropByDirection(dir), (ent is NodeBlockEntity || ent is CableEntity)))
//        }
        val diff = pos.subtract(neighborPos)
        val dir = Direction.fromDelta(diff.x, diff.y, diff.z)!!.opposite
        val ent = level.getBlockEntity(neighborPos)
        val value = ent is NodeBlockEntity || ent is CableEntity
        level.setBlockAndUpdate(pos, state.setValue(getPropByDirection(dir), value))
    }
}