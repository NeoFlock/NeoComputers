package org.neoflock.neocomputers.block;

import dev.architectury.registry.menu.ExtendedMenuProvider
import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.EnumProperty.*
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.ScreenEntity
import org.neoflock.neocomputers.gui.menu.Menus
import org.neoflock.neocomputers.gui.menu.ScreenMenu
import org.neoflock.neocomputers.network.Networking
import kotlin.math.abs
import kotlin.math.max

class ScreenBlock() : NodeBlock() {
    companion object {
        val FACING_HORIZ: EnumProperty<Direction> = EnumProperty.create<Direction>("facing_horiz", Direction::class.java)
        val FACING_VERTI: IntegerProperty = IntegerProperty.create("facing_verti", 0, 2) // "Integer" property doesnt accept values below 0, also death to enums, long live magic numbers
        val ENERGY: Long = 5
    }

    init {
        registerDefaultState(stateDefinition.any().setValue(FACING_HORIZ, Direction.NORTH).setValue(FACING_VERTI, 1))
    }

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        val scr = ScreenEntity(blockPos, blockState)
        scr.initNetworking()
        return scr
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide) {
            val screenState = level.getBlockEntity(blockPos, BlockEntities.SCREEN_ENTITY.get()).get()
            if(!screenState.node.consumeEnergy(ENERGY)) {
                player.sendSystemMessage(Component.literal("Not enough power."))
                return InteractionResult.SUCCESS
            };
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos, BlockEntities.SCREEN_ENTITY.get()).get()
            NodeSynchronizer.registerPlayerScreen(sp, ent)
            MenuRegistry.openExtendedMenu(sp, object : ExtendedMenuProvider {
                override fun getDisplayName(): Component = Component.literal("SCREEEEEN!")
                override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
//                    return Menus.SCREEN_MENU.get().create(i, inventory);
                    return ScreenMenu(i, inventory, ent)
                }

                override fun saveExtraData(buf: FriendlyByteBuf?) {
                    buf!!.writeBlockPos(blockPos)
                }
            })
        }
        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING_HORIZ)
        builder.add(FACING_VERTI)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val horiz = context.horizontalDirection
        val looking = context.player!!.lookAngle

        val biggest = max(max(abs(looking.y), abs(looking.z)), abs(looking.x))

        return super.getStateForPlacement(context)!!
            .setValue(FACING_HORIZ, horiz.opposite)
            .setValue(FACING_VERTI, if (biggest != abs(looking.y)) 1 else if (looking.y < 0) 2 else 0 )

//        val dirs = context.nearestLookingDirections
//        context.
//        return when (face) {
//            Direction.UP -> super.getStateForPlacement(context)!!.setValue(FACING_HORIZ, looking.opposite).setValue(FACING_VERTI, 2)
//            Direction.DOWN -> super.getStateForPlacement(context)!!.setValue(FACING_HORIZ, looking.opposite).setValue(FACING_VERTI, 0)
//            else -> super.getStateForPlacement(context)!!.setValue(FACING_HORIZ, looking.opposite).setValue(FACING_VERTI, 1)
//        }
    }
}