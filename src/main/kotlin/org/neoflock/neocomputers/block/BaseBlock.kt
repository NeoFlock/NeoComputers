package org.neoflock.neocomputers.block

import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import org.neoflock.neocomputers.NeoComputers
import java.util.function.Supplier
import com.google.common.base.Suppliers
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.neoflock.neocomputers.item.Tabs

open class BaseBlock(properties: Properties = Properties.of()) : Block(properties) { // TODO: create a TieredBaseBlock class that extends this or something
    // val tier: Int

    companion object Registry {
        fun register(name: String, sup: Supplier<BaseBlock>): RegistrySupplier<Block> = Blocks.BLOCKS.register(name, sup);
    }

    fun getBlockItem() : BlockItem {
        return BlockItem(this, Item.Properties().`arch$tab`(Tabs.TAB))
    }
}