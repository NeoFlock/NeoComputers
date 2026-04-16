package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import kotlin.math.min

open class CapacitorEntity(val capacity: Long, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : NodeBlockEntity(type, pos, state) {
    var amountStored: Long = 0

    override val node = object : Networking.Node() {
        override fun getPowerRole() = PowerRole.STORAGE
        override fun getEnergy() = amountStored
        override fun getEnergyCapacity() = capacity
        override fun giveEnergy(amount: Long): Long {
            val given = min(amount, capacity - amountStored)
            amountStored += given
            return given
        }

        override fun withdrawEnergy(amount: Long): Long {
            val taken = min(amount, amountStored)
            amountStored -= taken
            return taken
        }
    }
}

class CapacitorEntityTier1(pos: BlockPos, state: BlockState): CapacitorEntity(20000, BlockEntities.CAPACITOR_ENTITY.get(), pos, state)
class CapacitorEntityTier2(pos: BlockPos, state: BlockState): CapacitorEntity(50000, BlockEntities.CAPACITOR2_ENTITY.get(), pos, state)
class CapacitorEntityTier3(pos: BlockPos, state: BlockState): CapacitorEntity(100000, BlockEntities.CAPACITOR3_ENTITY.get(), pos, state)

class CapacitorBlock(val tier: Int) : NodeBlock()  {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        val cap: CapacitorEntity = when(tier) {
            1 -> CapacitorEntityTier1(blockPos, blockState)
            2 -> CapacitorEntityTier2(blockPos, blockState)
            3 -> CapacitorEntityTier3(blockPos, blockState)
            else -> throw UnsupportedOperationException("unsupported tier: $tier")
        }
        return cap.initNetworking()
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(!level.isClientSide()) {
            val sp = player as ServerPlayer
            val ent = level.getBlockEntity(blockPos)
            if(ent is CapacitorEntity) {
                if(sp.isCrouching) ent.amountStored++
                val msg = PlayerChatMessage.system("energy: ${ent.amountStored} / ${ent.capacity} (${ent.computeEdges().size} edges, ${ent.node.getReachable().size} connected)")
                sp.sendChatMessage(OutgoingChatMessage.create(msg), false, ChatType.bind(ChatType.CHAT, player))
            }
        }
        return InteractionResult.SUCCESS;
    }
}