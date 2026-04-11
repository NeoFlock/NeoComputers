package org.neoflock.neocomputers.block

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier

open class BaseBlock : Block { // TODO: create a TieredBaseBlock class that extends this or something
    // val tier: Int
    
    constructor(name: String):super(
        BlockBehaviour.Properties.of()
            .setId(ResourceKey
            .create(Registries.BLOCK, Identifier.fromNamespaceAndPath(NeoComputers.MODID, name))))

    companion object Registry {
        fun register(name: String, sup: Supplier<BaseBlock>): RegistrySupplier<Block> = Blocks.BLOCKS.register(name, sup);
    }
}