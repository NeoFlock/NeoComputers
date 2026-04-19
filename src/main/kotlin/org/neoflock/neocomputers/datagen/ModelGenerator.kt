package org.neoflock.neocomputers.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import org.neoflock.neocomputers.block.Blocks

class ModelGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
        blockStateModelGenerator.createGenericCube(Blocks.CASE_BLOCK.get())

    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
    }

}