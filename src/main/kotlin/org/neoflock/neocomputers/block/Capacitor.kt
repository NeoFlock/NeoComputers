package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.entity.NodeEntity
import org.neoflock.neocomputers.network.Networking

class CapacitorEntity(pos: BlockPos, state: BlockState) : NodeEntity(BlockEntities.CAPACITOR_ENTITY.get(), pos, state) {
    var amountStored: Double = 0.0
    val capacity = 20000.0

    val netNode = object : Networking.Node() {
        override fun isProducer() = true
        override fun getEnergy() = amountStored
        override fun maxEnergyCapacity(): Double = capacity
        override fun setEnergy(energy: Double) {
            amountStored = energy
        }
    }

    override fun getNode() = netNode
}

class CapacitorBlock : BaseBlock("capacitor"), EntityBlock {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        val cap = CapacitorEntity(blockPos, blockState)
        cap.syncReachable()
        Networking.addNode(cap.getNode())
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
                val msg = PlayerChatMessage.system("energy: ${cap.amountStored} / ${cap.capacity} (${cap.getReachableNodes().size} reachable, ${cap.getNode().getReachable().size} connected)")
                sp.sendChatMessage(OutgoingChatMessage.create(msg), false, ChatType.bind(ChatType.CHAT, player))
            }
        }
        return InteractionResult.SUCCESS;
    }
}