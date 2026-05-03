package org.neoflock.neocomputers.entity.render

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.LightLayer
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.lighting.BlockLightEngine
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.RackEntity
import org.neoflock.neocomputers.item.RackItem

class RackEntityRenderer(val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<RackEntity> {

    override fun render(ent: RackEntity, partialTick: Float, poseStack: PoseStack, source: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        poseStack.pushPose()
        poseStack.translate(1/16f, 11/16f, 1/16f)

        val render_slot = 2 // this is purely temporary type shit like true alpha shit, anyway it go to 0-3, change and test it if you want
        poseStack.translate(0f, (render_slot)*-3/16f, 0f)
        val server = object : RackItem {}
        server.render(source, poseStack, packedLight) // who knows atp
        poseStack.popPose()
    }
}