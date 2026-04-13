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

    val SCREEN_ENTITY: RegistrySupplier<BlockEntityType<ScreenEntity>> = BLOCKENTITIES.register("screen_entity") {
        BlockEntityType(
            ::ScreenEntity, mutableSetOf(Blocks.SCREEN_BLOCK.get()), BullshitFix()
        )
    }
    val CAPACITOR_ENTITY: RegistrySupplier<BlockEntityType<CapacitorEntity>> = BLOCKENTITIES.register("capacitor_entity") {
        BlockEntityType(
            ::CapacitorEntity, mutableSetOf(Blocks.CAPACITOR_BLOCK.get()), BullshitFix()
        )
    }
    val SOLARGEN_ENTITY: RegistrySupplier<BlockEntityType<SolarGeneratorBlockEntity>> = BLOCKENTITIES.register("solargen_entity") {
        BlockEntityType(
            ::SolarGeneratorBlockEntity, mutableSetOf(Blocks.SOLARGEN_BLOCK.get()), BullshitFix()
        )
    }
    val COMBUSTGEN_ENTITY: RegistrySupplier<BlockEntityType<CombustionGeneratorBlockEntity>> = BLOCKENTITIES.register("combustgen_entity") {
        BlockEntityType(
            ::CombustionGeneratorBlockEntity, mutableSetOf(Blocks.COMBUSTGEN_BLOCK.get()), BullshitFix()
        )
    }

    fun registerPowerBlocks() {
        PowerManager.registerPowerBlockEntity(CAPACITOR_ENTITY.get())
        PowerManager.registerPowerBlockEntity(COMBUSTGEN_ENTITY.get())
    }
}