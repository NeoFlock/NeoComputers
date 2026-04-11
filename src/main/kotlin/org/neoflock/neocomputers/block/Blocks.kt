package org.neoflock.neocomputers.block

import com.google.common.base.Suppliers
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
// import org.neoflock.neocomputers.item.Tabs
// import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.BaseBlock
import org.neoflock.neocomputers.item.Items
import org.neoflock.neocomputers.item.Tabs
import java.util.function.Consumer
import java.util.function.Supplier

object Blocks {


    val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(NeoComputers.MODID, Registries.BLOCK)
    val TEST_BLOCK: RegistrySupplier<Block> = BaseBlock.register("test") { BaseBlock("test") }
    val SCREEN_BLOCK: RegistrySupplier<Block> = BaseBlock.register("screen") { ScreenBlock() }

    fun registerBlockItems() {
        BLOCKS.forEach(Consumer { sup: RegistrySupplier<Block> ->
            Items.ITEMS.register(sup.id.path) { BlockItem(sup.get()!!, Item.Properties().`arch$tab`(Tabs.TAB).setId(ResourceKey.create(Registries.ITEM, sup.id)))}
        })
    }
}