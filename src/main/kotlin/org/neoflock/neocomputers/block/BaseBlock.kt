package org.neoflock.neocomputers.block

import net.minecraft.world.level.block.Block

class BaseBlock : Block {
    val tier: Int
    
    constructor(tier: Int): super(Properties.of()) {
        this.tier = tier
    }
}