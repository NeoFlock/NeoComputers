package org.neoflock.neocomputers.block

import net.minecraft.world.level.block.Block

class BaseBlock : Block {
    protected val tier: Int
    constructor(tier: Int): super(Properties.of()) {
        this.tier = tier
    }

    public fun getTier(): Int {
        return tier
    }
}