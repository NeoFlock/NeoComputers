package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.network.Networking
import org.neoflock.neocomputers.network.PowerRole
import kotlin.math.min

class CapacitorEntity(pos: BlockPos, state: BlockState) : NodeBlockEntity(BlockEntities.CAPACITOR_ENTITY.get(), pos, state) {
    var amountStored: Long = 0
    val capacity: Long = 20000

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

class CapacitorBlock : NodeBlock()  {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        val cap = CapacitorEntity(blockPos, blockState)
        cap.initNetworking()
        return cap
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
            val ent = level.getBlockEntity(blockPos, BlockEntities.CAPACITOR_ENTITY.get())
            if(ent.isPresent()) {
                val cap = ent.get()
                if(sp.isCrouching()) cap.amountStored++
                val msg = PlayerChatMessage.system("energy: ${cap.amountStored} / ${cap.capacity} (${cap.computeEdges().size} edges, ${cap.node.getReachable().size} connected)")
                sp.sendChatMessage(OutgoingChatMessage.create(msg), false, ChatType.bind(ChatType.CHAT, player))
            }
        }
        return InteractionResult.SUCCESS;
    }
}