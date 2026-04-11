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
    // val CASE: MutableList<RegistrySupplier<Block?>?>? =
    //     BaseBlock.register(intArrayOf(0, 1, 2), "case", { tier -> CaseBlock(tier) })


    val BLOCKS: DeferredRegister<Block?> = DeferredRegister.create(NeoComputers.MODID, Registries.BLOCK)
    // val TESTBLOCK : RegistrySupplier<Block> = registerBlock("test")
    val TESTBLOCK: RegistrySupplier<Block> = BLOCKS.register("test") { Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(NeoComputers.MODID, "test")))) }


    // public static final RegistrySupplier<Block> CASE0 = BLOCKS.register("case0", () -> new CaseBlock(0));
    // public static final RegistrySupplier<Block> CASE1 = BLOCKS.register("case1", () -> new CaseBlock(1));
    // public static final RegistrySupplier<Block> CASE2 = BLOCKS.register("case2", () -> new CaseBlock(2));
    // public static final RegistrySupplier<Block> CABLE = BLOCKS.register("cable", () -> new CableBlock());
    // val SCREEN: RegistrySupplier<Block?>? = BLOCKS.register<Block?>("screen", Supplier { ScreenBlock() })
    // val CABLE: RegistrySupplier<Block?>? = BLOCKS.register<Block?>("cable", Supplier { CableBlock() })

    // fun registerBlock(name: String): RegistrySupplier<Block> {
    //     // var Registrar<Item> items = MANAGER.get().get(Registries.ITEM);
    //     var blocks: Registrar<Block> = NeoComputers.MANAGER.get().get(Registries.BLOCK);
    //     return blocks.register(Identifier.fromNamespaceAndPath(NeoComputers.MODID, name)) {
    //         Block(
    //             BlockBehaviour.Properties.of().setId(
    //                 ResourceKey.create(
    //                     Registries.BLOCK,
    //                     Identifier.fromNamespaceAndPath(NeoComputers.MODID, name)
    //                 )
    //             )
    //         )
    //     }
    // }

    fun registerBlockItems() {
        BLOCKS.forEach(Consumer { sup: RegistrySupplier<Block?>? ->
            NeoComputers.LOGGER.info("mango viagra")
            // TODO: base blocks
            // if (blk instanceof BaseBlock) {
            //     Items.ITEMS.register(sup.getId().getPath(), () -> new BaseBlock.BaseBlockItem(blk, new Item.Properties().arch$tab(Tabs.TAB)));
            // } else {
            //     Items.ITEMS.register(sup.getId().getPath(), () -> new BlockItem(blk, new Item.Properties().arch$tab(Tabs.TAB)));
            // }
            Items.ITEMS.register(sup!!.id.path) { BlockItem(sup.get()!!, Item.Properties().`arch$tab`(Tabs.TAB).setId(ResourceKey.create(Registries.ITEM, sup.id)))}
        })
    }
}