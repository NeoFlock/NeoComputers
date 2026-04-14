package org.neoflock.neocomputers.block

import dev.architectury.registry.menu.MenuRegistry
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.SolarGeneratorBlockEntity
import org.neoflock.neocomputers.entity.CombustionGeneratorBlockEntity

class SolarGeneratorBlock : NodeBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = SolarGeneratorBlockEntity(blockPos, blockState).initNetworking()
}

// TODO: make it glow when burning
class CombustionGeneratorBlock : NodeBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity = CombustionGeneratorBlockEntity(blockPos, blockState).initNetworking()

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide()) {
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos, BlockEntities.COMBUSTGEN_ENTITY.get()).get()
            MenuRegistry.openMenu(sp, ent)
        }
        return InteractionResult.SUCCESS
    }
}
