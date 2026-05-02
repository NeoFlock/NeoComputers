package org.neoflock.neocomputers.entity.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.client.resources.model.ModelState
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.model.RobotModel
import org.neoflock.neocomputers.entity.RobotEntity
import kotlin.math.sin

class RobotEntityRenderer(val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<RobotEntity> {
    val atlas: (Material) -> TextureAtlasSprite = { m ->
        Minecraft.getInstance().getTextureAtlas(m.atlasLocation()).apply(m.texture())
    }
    val loc: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "robot")
    var model: BakedModel? = Minecraft.getInstance().modelManager.getModel(ModelResourceLocation.inventory(loc))
    val renderer: ModelBlockRenderer = ModelBlockRenderer(Minecraft.getInstance().blockColors) // so ass

    override fun render(ent: RobotEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {

        poseStack.pushPose()
        poseStack.translate(0f, sin(ent.level!!.dayTime.toFloat()/20F)*0.02F, 0f)
        val buffer = bufferSource.getBuffer(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS))

        renderer.renderModel(poseStack.last(), buffer, ent.blockState, model!!, 1f, 1f, 1f, packedLight, packedOverlay)

        poseStack.popPose()
    }

}