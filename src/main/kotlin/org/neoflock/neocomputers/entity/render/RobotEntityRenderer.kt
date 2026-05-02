package org.neoflock.neocomputers.entity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.CompositeState
import net.minecraft.client.renderer.block.ModelBlockRenderer
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.LivingEntityRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.client.resources.model.ModelState
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3
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

    val STREAK_RENDER_TYPE = RenderType.create("nc_robot_streak", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE,
        CompositeState.builder()
            .setShaderState(RenderStateShard.ShaderStateShard { GameRenderer.getPositionTexColorShader() })
            .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
            .setTextureState(RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/robot.png"), false, false))
            .createCompositeState(false))

    override fun render(ent: RobotEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        poseStack.pushPose()
//        poseStack.translate(0f, sin(ent.level!!.dayTime.toFloat()/20F)*0.02F, 0f)
        val modelbuffer = bufferSource.getBuffer(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS))
        val colorbuffer = bufferSource.getBuffer(STREAK_RENDER_TYPE)

        val col = DyeColor.LIGHT_GRAY.fireworkColor
        val red = ((col and 0xFF0000) shr 8*2) / 255f
        val green = ((col and 0xFF00) shr 8) / 255f
        val blue  = ((col and 0xFF)) / 255f

        renderer.renderModel(poseStack.last(), modelbuffer, ent.blockState, model!!, red, green, blue, packedLight, packedOverlay)
        renderLight(poseStack, colorbuffer, (ent.level!!.dayTime%16).toInt())


        // TODO: crafting table and chest little models

        poseStack.popPose()
        renderTag(ent, Component.literal(ent.name), poseStack, bufferSource, packedLight, partialTick)
    }

    // offset is 0-15
    fun renderLight(poseStack: PoseStack, buffer: VertexConsumer, offset: Int) {
        poseStack.pushPose()

        val u1 = 0.5f
        val v1 = 0.5f + offset*1/32f
        val u2 = 1F
        val v2 = 17/32f + offset*1/32f

        for (i in 0..3) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(90f), 0.5f, 0.5f, 0.5f)

            buffer.addVertex(poseStack.last(),0.1f+2/16f, 7/16f, 0.9f-2/16f).setColor(1f, 0f, 0f, 1f).setUv(u2, v2)
            buffer.addVertex(poseStack.last(),0.1f+2/16f, 9/16f, 0.9f-2/16f).setColor(1f, 0f, 0f, 1f).setUv(u2, v1)
            buffer.addVertex(poseStack.last(),0.1f+2/16f, 9/16f, 0.1f+2/16f).setColor(1f, 0f, 0f, 1f).setUv(u1, v1)
            buffer.addVertex(poseStack.last(),0.1f+2/16f, 7/16f, 0.1f+2/16f).setColor(1f, 0f, 0f, 1f).setUv(u1, v2)
        }

        poseStack.popPose()


    }

    fun renderTag(ent: RobotEntity, name: Component, stack: PoseStack, source: MultiBufferSource, light: Int, ptick: Float) {
        val d = Minecraft.getInstance().cameraEntity!!.distanceToSqr(ent.blockPos.center)
        if (d > 4096.0) return

        val vec = Vec3(0.5, 20 / 16.0, 0.5)

        stack.pushPose()
        stack.translate(vec.x, vec.y, vec.z)
//        stack.mulPose(context.entityRenderer.cameraOrientation())
        stack.scale(0.025F, -0.025F, 0.025F)
        val opacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F)
        val alpha: Int = (opacity * 255.0f).toInt() shl 24
//        val alpha = 255
        val halfwidth = (-context.font.width(name)) / 2;
        RenderSystem.disableDepthTest()
        context.font.drawInBatch(name, halfwidth.toFloat(), 2f, 0xFFFFFF, false, stack.last().pose(), source, Font.DisplayMode.SEE_THROUGH, alpha, light)

        RenderSystem.enableDepthTest()
        stack.popPose()
    }

}