package org.neoflock.neocomputers.block

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.RackEntity
import org.neoflock.neocomputers.network.NodeSynchronizer

class RackBlock : DeviceBlock(Properties.of().noOcclusion()), EntityBlock {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return RackEntity(pos, state)
    }

//    override fun getShape(
//        state: BlockState,
//        level: BlockGetter,
//        pos: BlockPos,
//        context: CollisionContext
//    ): VoxelShape? {
//        return Shapes.box(0.0,0.0,0.0,0.01,0.01,0.01)
//    }

//    override fun getRenderShape(state: BlockState): RenderShape? {
//        return RenderShape
//    }


    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult? {
        val res = hitResult.location
        val ent = level.getBlockEntity(pos, BlockEntities.RACK_ENTITY.get()).get()
        if(res.x == 18.0) { // TODO: handle rotation
            NeoComputers.LOGGER.info("{} > {} > {}, {} > {} > {}", pos.z+15/16f, res.z, pos.z+1/16f, pos.y+14/16f, res.y, pos.y+2/16f)
            if (pos.z + 15 / 16f > res.z && res.z > pos.z + 1 / 16f && pos.y + 14 / 16f > res.y && res.y > pos.y + 2 / 16f) {
                var rack = 0
                rack += if(res.y < pos.y+5/16f) 1 else 0
                rack += if(res.y < pos.y+8/16f) 1 else 0
                rack += if(res.y < pos.y+12/16f) 1 else 0

                player.sendSystemMessage(Component.literal(String.format("Hit server #%d", rack))) // TODO: call some RackItem method
                return InteractionResult.SUCCESS
            }
        }

        if (!level.isClientSide) {
            MenuRegistry.openExtendedMenu(player as ServerPlayer, ent)
            NodeSynchronizer.registerPlayerScreen(player as ServerPlayer, ent.node)
        }
        return InteractionResult.SUCCESS
    }
}