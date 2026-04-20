package org.neoflock.neocomputers.network

import net.minecraft.world.level.block.entity.BlockEntityType
//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.neoflock.neocomputers.block.NodeBlockEntity
import team.reborn.energy.api.EnergyStorage;
//?}

// our soul purpose is to fuse bullshit power APIs together
// the NodeBlockEntity and Node given us a way to get power from a block, we just
// need to tell mods how to do it as well
object PowerManager {
    fun<T: NodeBlockEntity> registerPowerBlockEntity(blockEntityType: BlockEntityType<T>) {
        //? if fabric {
        EnergyStorage.SIDED.registerForBlockEntity({
            entity, dir -> object : EnergyStorage {
                override fun getAmount() = entity.node.energy
                override fun getCapacity() = entity.node.energyCapacity
                override fun supportsExtraction() = entity.node.powerRole != PowerRole.CONSUMER && entity.node.energyCapacity > 0
                override fun supportsInsertion() = entity.node.powerRole != PowerRole.GENERATOR
                override fun extract(maxAmount: Long, transaction: TransactionContext?): Long {
                    if(entity.node.powerRole == PowerRole.CONSUMER) return 0
                    val taken = entity.node.withdrawEnergy(maxAmount)
                    transaction?.addCloseCallback {
                        ctx, res -> if(res.wasAborted() || !res.wasCommitted()) entity.node.giveEnergy(taken)
                    }
                    return taken
                }
                override fun insert(maxAmount: Long, transaction: TransactionContext?): Long {
                    if(entity.node.powerRole == PowerRole.GENERATOR) return 0
                    val given = entity.node.giveEnergy(maxAmount)
                    transaction?.addCloseCallback { ctx, res ->
                        if (res.wasAborted() || !res.wasCommitted()) entity.node.withdrawEnergy(given)
                    }
                    return given
                }
            }
        }, blockEntityType);
        //?}
    }
}