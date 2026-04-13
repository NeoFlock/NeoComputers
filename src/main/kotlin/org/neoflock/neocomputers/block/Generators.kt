package org.neoflock.neocomputers.block

import net.minecraft.client.resources.sounds.Sound
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.SolarGeneratorBlockEntity
import org.neoflock.neocomputers.entity.CombustionGeneratorBlockEntity

class SolarGeneratorBlock : BaseBlock(), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return SolarGeneratorBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is SolarGeneratorBlockEntity) return;
                blockEntity.giveSolarPower();
            }
        }
    }
}

// TODO: make it glow when burning
class CombustionGeneratorBlock : Block(BlockBehaviour.Properties.of()), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        return CombustionGeneratorBlockEntity(blockPos, blockState)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return object : BlockEntityTicker<T> {
            override fun tick(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: T) {
                if(blockEntity !is CombustionGeneratorBlockEntity) return;
                blockEntity.burnFuelForEnergy();
            }
        }
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult? {
        if(!level.isClientSide()) {
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos, BlockEntities.COMBUSTGEN_ENTITY.get())
            if(ent.isPresent) {
                val bust = ent.get()
                val fuel = bust.stacks[0]
                val msg = PlayerChatMessage.system("${fuel.displayName.string} x ${fuel.count} (${bust.node.getEnergy()} / ${bust.node.getEnergyCapacity()} J)")
                sp.sendChatMessage(OutgoingChatMessage.create(msg), false, ChatType.bind(ChatType.CHAT, player))
            }
        }
        return InteractionResult.SUCCESS;
    }
}
