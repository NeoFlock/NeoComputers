package org.neoflock.neocomputers.gui.widget

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.MachineEntity
import org.neoflock.neocomputers.item.ComponentItem

// Sort of a mis-nomer, does not need to be associated with components specifically

object ComponentRoles {
    // For card slots
    val CARD = "card"
    // For memory slots
    val MEMORY = "memory"
    // For storage slots, aside from EEPROMs and floppys
    val STORAGE = "storage"
    // For floppy drive slots
    val FLOPPY = "floppy"
    // For EEPROM slots
    val FIRMWARE = "firmware"
    // For CPU slot
    val COMPUTE = "compute"
    // For component bus
    val BUS = "bus"
    val TOOL = "tool"
    val UPGRADE = "upgrade"
    val CONTAINER = "container"
    val TABLET = "tablet"
    val RACK_MOUNTABLE = "rack"
    // Conventional network cards, like LAN, WLAN0, WLAN1, etc.
    val NETWORK = "network"
    // Internet cards
    val INET = "internet"

    val MISSING_ROLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/slots/na.png")

    val textureMap = mutableMapOf<String, ResourceLocation>()

    fun mapTexture(role: String, texture: ResourceLocation) {
        textureMap[role] = texture
    }

    fun getTextureFor(role: String) = textureMap[role] ?: MISSING_ROLE_TEXTURE

    fun mapDefaultTextures() {
        val core = mapOf(
            CARD to "card",
            BUS to "component_bus",
            CONTAINER to "container",
            COMPUTE to "cpu",
            FIRMWARE to "eeprom",
            FLOPPY to "floppy",
            STORAGE to "hdd",
            MEMORY to "memory",
            TABLET to "tablet",
            TOOL to "tool",
            UPGRADE to "upgrade",
            RACK_MOUNTABLE to "rack_mountable",
            // TODO: give them proper textures
            NETWORK to "card",
            INET to "card",
        )
        for((role, tex) in core) {
            mapTexture(role, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/gui/slots/$tex.png"))
        }
    }
}

data class ComponentSlotRequirement(val tier: Int, val role: String) {
    fun allowsItemStack(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        if(item !is ComponentItem) return false
        if(tier > 0 && item.getComponentTier(itemStack) > tier) return false
        return item.getComponentRoles(itemStack).contains(role)
    }
}

// Tier 0 allows ALL tiers, making it completely untiered.
// Role determines what the role is.
class ComponentSlot(container: Container, slot: Int, x: Int, y: Int, val machine: MachineEntity?, val requirement: ComponentSlotRequirement): DynamicSlot(container, slot, x, y) {
    override fun draw(graphics: GuiGraphics, relX: Int, relY: Int, mouseX: Int, mouseY: Int) {
        super.draw(graphics, relX, relY, mouseX, mouseY)
        if(!hasItem()) {
            RenderSystem.enableBlend()
            RenderSystem.setShaderTexture(0, ComponentRoles.getTextureFor(requirement.role))
            RenderSystem.setShader { GameRenderer.getPositionTexShader() }
            drawQuad(relX + x - 1, relY + y - 1, 18, 18, 0F, 0F, 15F, 15F)
            if (requirement.tier > 0) {
                RenderSystem.setShaderTexture(
                    0,
                    ResourceLocation.fromNamespaceAndPath(
                        NeoComputers.MODID,
                        "textures/gui/slots/tier${requirement.tier - 1}.png"
                    )
                )
                RenderSystem.setShader { GameRenderer.getPositionTexShader() }
                drawQuad(relX + x - 1, relY + y - 1, 18, 18, 0F, 0F, 15F, 15F)
            }
            RenderSystem.disableBlend()
        }
    }

    override fun mayPlace(itemStack: ItemStack): Boolean {
        if(!requirement.allowsItemStack(itemStack)) return false
        return super.mayPlace(itemStack)
    }

    override fun set(itemStack: ItemStack) {
        super.set(itemStack)
        if(machine != null) {
            val item = itemStack.item
            if (item is ComponentItem) {
                item.whenComponentPlaced(itemStack, machine, requirement.role)
            }
        }
    }

    override fun onTake(player: Player, itemStack: ItemStack) {
        if(machine != null) {
            val item = itemStack.item
            if (item is ComponentItem) {
                item.whenComponentTaken(itemStack, machine, requirement.role)
            }
        }
        super.onTake(player, itemStack)
    }

    override fun getMaxStackSize(): Int = 1
    override fun getMaxStackSize(itemStack: ItemStack): Int = 1
}