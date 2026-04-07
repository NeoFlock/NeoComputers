package org.libreflock.neocomputers.block

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.libreflock.neocomputers.item.Items
import org.libreflock.neocomputers.item.Tabs
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.BaseBlock
import java.util.function.Consumer
import java.util.function.Supplier

object Blocks {
    val BLOCKS: DeferredRegister<Block?> = DeferredRegister.create(NeoComputers.MODID, Registries.BLOCK)


    val CASE: MutableList<RegistrySupplier<Block?>?>? =
        BaseBlock.register(intArrayOf(0, 1, 2), "case", { tier -> CaseBlock(tier) })

    // public static final RegistrySupplier<Block> CASE0 = BLOCKS.register("case0", () -> new CaseBlock(0));
    // public static final RegistrySupplier<Block> CASE1 = BLOCKS.register("case1", () -> new CaseBlock(1));
    // public static final RegistrySupplier<Block> CASE2 = BLOCKS.register("case2", () -> new CaseBlock(2));
    // public static final RegistrySupplier<Block> CABLE = BLOCKS.register("cable", () -> new CableBlock());
    val SCREEN: RegistrySupplier<Block?>? = BLOCKS.register<Block?>("screen", Supplier { ScreenBlock() })
    val CABLE: RegistrySupplier<Block?>? = BLOCKS.register<Block?>("cable", Supplier { CableBlock() })

    fun registerBlockItems() {
        BLOCKS.forEach(Consumer { sup: RegistrySupplier<Block?>? ->
            // sup.pre
            // sup.((blk) -> {
            // NeoComputers.LOGGER.info(blk.getDescriptionId());
            // if (blk instanceof BaseBlock) {
            //     Items.ITEMS.register(sup.getId().getPath(), () -> new BaseBlock.BaseBlockItem(blk, new Item.Properties().arch$tab(Tabs.TAB)));
            // } else {
            //     Items.ITEMS.register(sup.getId().getPath(), () -> new BlockItem(blk, new Item.Properties().arch$tab(Tabs.TAB)));
            // }
            // });
            Items.ITEMS.register(sup!!.getId().getPath(), {
                if (sup.get() is BaseBlock) {
                    return@register BaseBlockItem(sup.get(), Item.Properties().`arch$tab`(Tabs.TAB))
                } else {
                    return@register BlockItem(sup.get(), Item.Properties().`arch$tab`(Tabs.TAB))
                }
            })
        })
    }
}