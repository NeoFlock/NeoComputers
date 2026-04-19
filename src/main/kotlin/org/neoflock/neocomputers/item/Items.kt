package org.neoflock.neocomputers.item

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.Item
import org.neoflock.neocomputers.NeoComputers

object Items {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(NeoComputers.MODID, Registries.ITEM)

    val MEM0 = ITEMS.register("memory0") { MemoryTier1() }
    val MEM1 = ITEMS.register("memory1") { MemoryTier1_5() }
    val MEM2 = ITEMS.register("memory2") { MemoryTier2() }
    val MEM3 = ITEMS.register("memory3") { MemoryTier2_5() }
    val MEM4 = ITEMS.register("memory4") { MemoryTier3() }
    val MEM5 = ITEMS.register("memory5") { MemoryTier3_5() }

    val EE0 = ITEMS.register("eeprom0") { EEPROM0() }

    val CPU0 = ITEMS.register("cpu0") { CPU0() }
    val CPU1 = ITEMS.register("cpu1") { CPU1() }
    val CPU2 = ITEMS.register("cpu2") { CPU2() }

    val CBUS0 = ITEMS.register("cbus0") { CBUS0() }
    val CBUS1 = ITEMS.register("cbus1") { CBUS1() }
    val CBUS2 = ITEMS.register("cbus2") { CBUS2() }
    val CBUS_CREATIVE = ITEMS.register("cbus_creative") { CBUSCreative() }

    val INET = ITEMS.register("inet") { InternetCard() }
    val TUNNEL = ITEMS.register("tunnel") { TunnelCard() }
    val LAN = ITEMS.register("lan") { LANCard() }
    val WLAN0 = ITEMS.register("wlan0") { WLANCard0() }
    val WLAN1 = ITEMS.register("wlan1") { WLANCard1() }

    val DATA0 = ITEMS.register("data0") { DataCard0() }
    val DATA1 = ITEMS.register("data1") { DataCard1() }
    val DATA2 = ITEMS.register("data2") { DataCard2() }

    val GPU0 = ITEMS.register("gpu0") { GPUCard0() }
    val GPU1 = ITEMS.register("gpu1") { GPUCard1() }
    val GPU2 = ITEMS.register("gpu2") { GPUCard2() }

    val HDD0 = ITEMS.register("hdd0") { HardDisk0() }
    val HDD1 = ITEMS.register("hdd1") { HardDisk1() }
    val HDD2 = ITEMS.register("hdd2") { HardDisk2() }
}