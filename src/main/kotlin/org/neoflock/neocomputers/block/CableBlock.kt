package org.neoflock.neocomputers.block

import net.minecraft.client.renderer.blockentity.PistonHeadRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.DyeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.CableEntity

class CableBlock() : BaseBlock(Properties.of()), EntityBlock {
    companion object {
        val NORTH = BooleanProperty.create("north")
        val EAST = BooleanProperty.create("east")
        val WEST = BooleanProperty.create("west")
        val SOUTH = BooleanProperty.create("south")
        val UP = BooleanProperty.create("up")
        val DOWN = BooleanProperty.create("down")

        val COLOR = EnumProperty<DyeColor>.create("color", DyeColor::class.java)

        val MIN = 0.375
        val MAX = 1-MIN

        val shapeCache: Array<VoxelShape?> = arrayOfNulls(Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size*Direction.entries.size)

        fun calcIdx(north: Boolean, south: Boolean, east: Boolean, west: Boolean, up: Boolean, down: Boolean): Int {
            var idx = if (down) 1 else 0
            idx += 2*(if (up) 1 else 0)
            idx += 4*(if (west) 1 else 0)
            idx += 8*(if (east) 1 else 0)
            idx += 16*(if (south) 1 else 0)
            idx += 32*(if (north) 1 else 0)
            return idx
        }

        fun makeShapes() {
            NeoComputers.LOGGER.info("[CABLE] recomputing shapes")
            for (north in arrayOf(false, true)) { // shut up
                for (south in arrayOf(false, true)) {
                    for (east in arrayOf(false, true)) {
                        for (west in arrayOf(false, true)) {
                            for (up in arrayOf(false, true)) {
                                for (down in arrayOf(false, true)) {
                                    val shape = makeShape(north, south, east, west, up, down)
                                    val idx = calcIdx(north, south, east,west, up, down)
                                    shapeCache[idx] = shape;
                                }
                            }
                        }
                    }
                }
            }
        }

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
        fun shouldConnect(pos: BlockPos, npos: BlockPos, level: Level): Boolean {
            val ent = level.getBlockEntity(npos)
            val blockState = level.getBlockState(pos)

            return ent is NodeBlockEntity || (ent is CableEntity &&
                    (level.getBlockState(npos).getValue(COLOR).equals(blockState.getValue(COLOR)) ||
                            blockState.getValue(COLOR).equals(DyeColor.LIGHT_GRAY) ||
                            level.getBlockState(npos).getValue(COLOR).equals(DyeColor.LIGHT_GRAY)))
//        val state: BlockState? = (ent is CableEntity && (level.getBlockState(neighborPos).getValue(COLOR).equals(state.getValue(COLOR)) || state.getValue(COLOR).equals(DyeColor.LIGHT_GRAY))
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
            .setValue(COLOR, DyeColor.LIGHT_GRAY)
        )
        makeShapes()
    }


    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder
            .add(NORTH)
            .add(EAST)
            .add(SOUTH)
            .add(WEST)
            .add(UP)
            .add(DOWN)
            .add(COLOR))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return CableEntity(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape? {
        val idx = calcIdx(state.getValue(NORTH), state.getValue(SOUTH), state.getValue(EAST), state.getValue(WEST), state.getValue(UP), state.getValue(DOWN))
        return shapeCache[idx];
    //        return makeShape(state.getValue(NORTH), state.getValue(SOUTH), state.getValue(EAST), state.getValue(WEST), state.getValue(UP), state.getValue(DOWN))
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
//        val value = ent is NodeBlockEntity || (ent is CableEntity && (level.getBlockState(neighborPos).getValue(COLOR).equals(state.getValue(COLOR)) || state.getValue(COLOR).equals(DyeColor.LIGHT_GRAY)))
        level.setBlockAndUpdate(pos, state.setValue(getPropByDirection(dir), shouldConnect(pos, neighborPos, level)))
    }

    override fun useItemOn(stack: ItemStack, state: BlockState, level: Level, pos: BlockPos, player: Player, hand: InteractionHand, hitResult: BlockHitResult): ItemInteractionResult? {
//        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        if (stack.item is DyeItem) {
            val dyeitem = stack.item as DyeItem
            level.setBlockAndUpdate(pos, state.setValue(COLOR, dyeitem.dyeColor))
            return ItemInteractionResult.SUCCESS
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }
}