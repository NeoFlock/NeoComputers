package org.neoflock.neocomputers.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import org.neoflock.neocomputers.NeoComputers

class NeoComputersDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack: FabricDataGenerator.Pack = generator.createPack()
        pack.addProvider(::ModelGenerator)
    }
}