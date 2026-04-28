package org.neoflock.neocomputers.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.OutgoingChatMessage
import net.minecraft.network.chat.PlayerChatMessage
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.neoflock.neocomputers.entity.BlockEntities
import org.neoflock.neocomputers.network.DeviceNode
import org.neoflock.neocomputers.network.PowerRole
import kotlin.math.min

open class CapacitorEntity(val capacity: Long, type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : DeviceBlockEntity(type, pos, state) {

    val deviceNode = object : DeviceNode() {
        override var powerRole = PowerRole.STORAGE
        override var energyCapacity: Long = capacity

        override fun writeFullStateCommit(buf: FriendlyByteBuf) {
            super.writeFullStateCommit(buf)
            buf.writeVarLong(energy)
        }

        override fun processCommit(buf: FriendlyByteBuf) {
            super.processCommit(buf)
            energy = buf.readVarLong()
        }
    }

    // TODO: cache list
    override fun getDeviceNodes() = listOf(deviceNode)
    override fun getNodeFromSide(directionToRequester: Direction) = deviceNode

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        deviceNode.energy = min(tag.getLong("energy"), deviceNode.energyCapacity)
    }

    override fun saveAdditional(compoundTag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(compoundTag, provider)
        compoundTag.putLong("energy", deviceNode.energy)
    }
}

class CapacitorEntityTier1(pos: BlockPos, state: BlockState): CapacitorEntity(20000, BlockEntities.CAPACITOR_ENTITY.get(), pos, state)
class CapacitorEntityTier2(pos: BlockPos, state: BlockState): CapacitorEntity(50000, BlockEntities.CAPACITOR2_ENTITY.get(), pos, state)
class CapacitorEntityTier3(pos: BlockPos, state: BlockState): CapacitorEntity(100000, BlockEntities.CAPACITOR3_ENTITY.get(), pos, state)

class CapacitorBlock(val tier: Int) : DeviceBlock()  {
    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity {
        val cap: CapacitorEntity = when(tier) {
            1 -> CapacitorEntityTier1(blockPos, blockState)
            2 -> CapacitorEntityTier2(blockPos, blockState)
            3 -> CapacitorEntityTier3(blockPos, blockState)
            else -> throw UnsupportedOperationException("unsupported tier: $tier")
        }
        return cap
    }

    override fun useWithoutItem(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if(level.isClientSide()) {
            val ent = level.getBlockEntity(blockPos)
            if(ent is CapacitorEntity) {
                if(player.isCrouching) ent.deviceNode.giveEnergy(1)
                val msg = PlayerChatMessage.system("energy: ${ent.deviceNode.energy} / ${ent.capacity} (${ent.deviceNode.connections.size} connections, ${ent.deviceNode.getReachable().size} reachable)")
                player.sendSystemMessage(OutgoingChatMessage.create(msg).content())
            }
        }
        return InteractionResult.SUCCESS
    }
}