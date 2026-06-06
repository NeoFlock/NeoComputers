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
                Items.ITEMS.forEach {
                    output.accept(ItemStack(it.get()))
                }

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
                    luaBios.set(DataComponents.ARCH, "Lua 5.2")
                    luaBios.set(DataComponents.EEPROM_CODE, codeBuf)
                    luaBios.set(DataComponents.EEPROM_CODESIZE, code.size)
                    output.accept(luaBios)
                } while(false)
            }
        }
    }
}