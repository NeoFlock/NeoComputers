package org.neoflock.neocomputers.entity;

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.Blocks

object BlockEntities {
    val BLOCKENTITIES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(NeoComputers.MODID, Registries.BLOCK_ENTITY_TYPE);

    val SCREEN_ENTITY: RegistrySupplier<BlockEntityType<ScreenEntity>> = BLOCKENTITIES.register("screen_entity") { BlockEntityType(::ScreenEntity, mutableSetOf(Blocks.SCREEN_BLOCK.get()))}
}