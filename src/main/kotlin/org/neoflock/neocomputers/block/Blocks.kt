package org.neoflock.neocomputers.block

import com.google.common.base.Suppliers
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.Registrar
import dev.architectury.registry.registries.RegistrarManager
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
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
    val SCREEN_BLOCK: RegistrySupplier<Block> = BaseBlock.register("screen") { ScreenBlock() }
    val CAPACITOR_BLOCK: RegistrySupplier<Block> = BaseBlock.register("capacitor") { CapacitorBlock(1) }
    val CAPACITOR_BLOCK2: RegistrySupplier<Block> = BaseBlock.register("capacitor2") { CapacitorBlock(2) }
    val CAPACITOR_BLOCK3: RegistrySupplier<Block> = BaseBlock.register("capacitor3") { CapacitorBlock(3) }
    val SOLARGEN_BLOCK: RegistrySupplier<Block> = BaseBlock.register("solargen") { SolarGeneratorBlock() }
    val COMBUSTGEN_BLOCK: RegistrySupplier<Block> = BaseBlock.register("combustgen") { CombustionGeneratorBlock() }
    val CASE_BLOCK: RegistrySupplier<Block> = BaseBlock.register("case") { CaseBlock() }
    val REDSTONEIO_BLOCK: RegistrySupplier<Block> = BaseBlock.register("redio") { RedstoneIOBlock() }
    val CABLE_BLOCK: RegistrySupplier<Block> = BaseBlock.register("cable") { CableBlock() }

    fun registerBlockItems() {
        BLOCKS.forEach(Consumer { sup: RegistrySupplier<Block> ->
            NeoComputers.LOGGER.info(sup.id.toString())
            val id = ResourceKey.create(Registries.ITEM, sup.id)
            Items.ITEMS.register(sup.id.path) { BlockItem(sup.get()!!, Item.Properties().`arch$tab`(Tabs.TAB))}
        })
    }
}