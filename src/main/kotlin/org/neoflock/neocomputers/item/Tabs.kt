package org.neoflock.neocomputers.item

import dev.architectury.registry.CreativeTabRegistry
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.neoflock.neocomputers.NeoComputers
import java.nio.ByteBuffer

object Tabs {
    val TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(NeoComputers.MODID, Registries.CREATIVE_MODE_TAB)

    val TAB = TABS.register("neocomputers_tab") {
        // its only experimental once they change it
        CreativeTabRegistry.create { builder ->
            builder.title(Component.literal("NeoComputers"))
            builder.icon {
                ItemStack(Items.MEM0.get())
            }
            builder.displayItems { parameters, output ->
                // TODO: get rid of arch$tab and this shi and replace with loop over items registry
                output.accept(ItemStack(Items.CPU0.get()))
                output.accept(ItemStack(Items.CPU1.get()))
                output.accept(ItemStack(Items.CPU2.get()))

                output.accept(ItemStack(Items.CBUS0.get()))
                output.accept(ItemStack(Items.CBUS1.get()))
                output.accept(ItemStack(Items.CBUS2.get()))
                output.accept(ItemStack(Items.CBUS_CREATIVE.get()))

                output.accept(ItemStack(Items.DATA0.get()))
                output.accept(ItemStack(Items.DATA1.get()))
                output.accept(ItemStack(Items.DATA2.get()))

                output.accept(ItemStack(Items.GPU0.get()))
                output.accept(ItemStack(Items.GPU1.get()))
                output.accept(ItemStack(Items.GPU2.get()))

                output.accept(ItemStack(Items.HDD0.get()))
                output.accept(ItemStack(Items.HDD1.get()))
                output.accept(ItemStack(Items.HDD2.get()))

                output.accept(ItemStack(Items.INET.get()))
                output.accept(ItemStack(Items.TUNNEL.get()))
                output.accept(ItemStack(Items.LAN.get()))
                output.accept(ItemStack(Items.WLAN0.get()))
                output.accept(ItemStack(Items.WLAN1.get()))

                output.accept(ItemStack(Items.REDIO0.get()))
                output.accept(ItemStack(Items.REDIO1.get()))

                output.accept(ItemStack(Items.EE0.get()))

                // Criminal black magic to put LuaBIOS EEPROM in the tabs
                do {
                    val luaBios = ItemStack(Items.EE0.get())
                    val res = Minecraft.getInstance().resourceManager.getResourceOrThrow(
                        ResourceLocation.fromNamespaceAndPath(
                            NeoComputers.MODID,
                            "lua/oc_bios.lua"
                        )
                    )
                    val stream = res.openAsReader()
                    val code = stream.readText().encodeToByteArray()
                    stream.close()
                    val codeBuf = ByteBuffer.allocate(code.size)
                    codeBuf.put(code)
                    luaBios.set(DataComponents.LABEL, "Lua BIOS")
                    luaBios.set(DataComponents.EEPROM_CODE, codeBuf)
                    luaBios.set(DataComponents.EEPROM_CODESIZE, code.size)
                    output.accept(luaBios)
                } while(false)
            }
        }
    }
}