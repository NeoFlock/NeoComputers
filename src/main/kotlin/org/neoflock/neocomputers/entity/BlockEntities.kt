package org.neoflock.neocomputers.entity;

import com.mojang.datafixers.types.templates.TypeTemplate
import com.mojang.serialization.Codec
import com.mojang.datafixers.types.Type as DataFixType
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.Blocks
import org.neoflock.neocomputers.block.CapacitorEntity
import org.neoflock.neocomputers.block.CapacitorEntityTier1
import org.neoflock.neocomputers.block.CapacitorEntityTier2
import org.neoflock.neocomputers.block.CapacitorEntityTier3
import org.neoflock.neocomputers.network.PowerManager

// complete fucking bullshit btw
class BullshitFix: DataFixType<Unit>() {
    override fun buildTemplate(): TypeTemplate? {
        return null
    }

    override fun buildCodec(): Codec<Unit?>? {
        return null
    }

    override fun equals(
        o: Any?,
        ignoreRecursionPoints: Boolean,
        checkIndex: Boolean
    ): Boolean {
        return o == this
    }

}

object BlockEntities {
    val BLOCKENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(NeoComputers.MODID, Registries.BLOCK_ENTITY_TYPE);

    val SCREEN_ENTITY: RegistrySupplier<BlockEntityType<ScreenEntity>> = BLOCKENTITIES.register("screen") {
        BlockEntityType(
            ::ScreenEntity, setOf(Blocks.SCREEN_BLOCK.get()), BullshitFix()
        )
    }
    val CAPACITOR_ENTITY: RegistrySupplier<BlockEntityType<CapacitorEntityTier1>> = BLOCKENTITIES.register("capacitor") {
        BlockEntityType(
            ::CapacitorEntityTier1, setOf(Blocks.CAPACITOR_BLOCK.get()), BullshitFix()
        )
    }
    val CAPACITOR2_ENTITY: RegistrySupplier<BlockEntityType<CapacitorEntityTier2>> = BLOCKENTITIES.register("capacitor2") {
        BlockEntityType(
            ::CapacitorEntityTier2, setOf(Blocks.CAPACITOR_BLOCK2.get()), BullshitFix()
        )
    }
    val CAPACITOR3_ENTITY: RegistrySupplier<BlockEntityType<CapacitorEntityTier3>> = BLOCKENTITIES.register("capacitor3") {
        BlockEntityType(
            ::CapacitorEntityTier3, setOf(Blocks.CAPACITOR_BLOCK3.get()), BullshitFix()
        )
    }
    val SOLARGEN_ENTITY: RegistrySupplier<BlockEntityType<SolarGeneratorBlockEntity>> = BLOCKENTITIES.register("solargen") {
        BlockEntityType(
            ::SolarGeneratorBlockEntity, setOf(Blocks.SOLARGEN_BLOCK.get()), BullshitFix()
        )
    }
    val COMBUSTGEN_ENTITY: RegistrySupplier<BlockEntityType<CombustionGeneratorBlockEntity>> = BLOCKENTITIES.register("combustgen") {
        BlockEntityType(
            ::CombustionGeneratorBlockEntity, setOf(Blocks.COMBUSTGEN_BLOCK.get()), BullshitFix()
        )
    }
    val REDSTONEIO_ENTITY: RegistrySupplier<BlockEntityType<CombustionGeneratorBlockEntity>> = BLOCKENTITIES.register("redio") {
        BlockEntityType(
            ::CombustionGeneratorBlockEntity, setOf(Blocks.REDSTONEIO_BLOCK.get()), BullshitFix()
        )
    }
    val CASE_ENTITY: RegistrySupplier<BlockEntityType<CaseBlockEntity>> = BLOCKENTITIES.register("case") {
        BlockEntityType(
            ::CaseBlockEntity, setOf(Blocks.CASE_BLOCK.get()), BullshitFix()
        )
    }

    val CABLE_ENTITY: RegistrySupplier<BlockEntityType<CableEntity>> = BLOCKENTITIES.register("cable") {
        BlockEntityType(
            ::CableEntity, setOf(Blocks.CABLE_BLOCK.get()), BullshitFix()
        )
    }

    fun registerPowerBlocks() {
        PowerManager.registerPowerBlockEntity(CAPACITOR_ENTITY.get())
        PowerManager.registerPowerBlockEntity(CAPACITOR2_ENTITY.get())
        PowerManager.registerPowerBlockEntity(CAPACITOR3_ENTITY.get())
        PowerManager.registerPowerBlockEntity(SOLARGEN_ENTITY.get())
        PowerManager.registerPowerBlockEntity(COMBUSTGEN_ENTITY.get())
        PowerManager.registerPowerBlockEntity(CASE_ENTITY.get())
    }
}