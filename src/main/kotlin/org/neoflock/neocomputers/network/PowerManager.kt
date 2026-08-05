package org.neoflock.neocomputers.network

import net.minecraft.world.level.block.entity.BlockEntityType
import org.neoflock.neocomputers.block.DeviceBlockEntity
//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.core.Direction
import team.reborn.energy.api.EnergyStorage;
//?}

// our soul purpose is to fuse bullshit power APIs together
// the NodeBlockEntity and Node given us a way to get power from a block, we just
// need to tell mods how to do it as well
object PowerManager {
    fun<T: DeviceBlockEntity> registerPowerDevice(blockEntityType: BlockEntityType<T>) {
        //? if fabric {
        EnergyStorage.SIDED.registerForBlockEntity({
                // TODO: as this is currently written, if the node instance changes and the mod cached the conversion, we're boned. Consider fixing it.
                entity, dir ->
                val node = entity.getNodeFromSide(dir ?: Direction.UP)
                if(node == null) null else object : EnergyStorage {
                    override fun getAmount() = node.energy
                    override fun getCapacity() = node.energyCapacity
                    override fun supportsExtraction() = node.powerRole != PowerRole.CONSUMER && node.energyCapacity > 0
                    override fun supportsInsertion() = node.powerRole != PowerRole.GENERATOR
                    override fun extract(maxAmount: Long, transaction: TransactionContext?): Long {
                        if(node.powerRole == PowerRole.CONSUMER) return 0
                        val taken = node.withdrawEnergy(maxAmount)
                        transaction?.addCloseCallback {
                                ctx, res -> if(res.wasAborted() || !res.wasCommitted()) node.giveEnergy(taken)
                        }
                        return taken
                    }
                    override fun insert(maxAmount: Long, transaction: TransactionContext?): Long {
                        if(node.powerRole == PowerRole.GENERATOR) return 0
                        val given = node.giveEnergy(maxAmount)
                        transaction?.addCloseCallback { ctx, res ->
                            if (res.wasAborted() || !res.wasCommitted()) node.withdrawEnergy(given)
                        }
                        return given
                    }
                }
        }, blockEntityType);
        //?}
    }
}