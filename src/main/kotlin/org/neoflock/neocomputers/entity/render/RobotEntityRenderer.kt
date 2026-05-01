package org.neoflock.neocomputers.entity.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
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
import net.minecraft.client.resources.model.ModelState
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.model.RobotModel
import org.neoflock.neocomputers.entity.RobotEntity
import kotlin.math.sin

class RobotEntityRenderer(val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<RobotEntity> {
//    val body: ModelPart = context.modelSet.bakeLayer(ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/robot"), "inventory"))
//    val body: BlockModel = BlockModel
    val atlas: (Material?) -> TextureAtlasSprite = { m ->
        Minecraft.getInstance().getTextureAtlas(m!!.atlasLocation()).apply(m!!.texture())
    }

    val loc: ResourceLocation = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "block/robot")
    var model: BakedModel? = RobotModel().bake(atlas, object : ModelState {})
    val renderer: ModelBlockRenderer = ModelBlockRenderer(Minecraft.getInstance().blockColors) // so ass

//    val RENDER_TYPE = RenderType.create("nc_case", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
//        RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder()
//            .setShaderState(RenderStateShard.RENDERTYPE_)
//            .createCompositeState(false))

//    val RENDER_TYPE = { tex: ResourceLocation -> RenderType.create("nc_robot", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS,
//        RenderType.SMALL_BUFFER_SIZE, RenderType.CompositeState.builder()
//            .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
//            .setLightmapState(RenderStateShard.LIGHTMAP)
//            .setTextureState(RenderStateShard.TextureStateShard(tex, false, false))
//            .createCompositeState(false)) }

    override fun render(ent: RobotEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
//        if (model == null) this.model = Minecraft.getInstance().modelManager.getModel(loc) // worst cant describe how much ts sux holy shit!!!!!
//        if (model == null) this.model =
//        NeoComputers.LOGGER.info(model.toString())
        poseStack.pushPose()
//        NeoComputers.LOGGER.info(sin(ent.level!!.dayTime.toFloat()/20F).toString())
        poseStack.translate(0f, sin(ent.level!!.dayTime.toFloat()/20F)*0.2F, 0f)
//        val buffer = bufferSource.getBuffer(RENDER_TYPE(ResourceLocation.withDefaultNamespace("textures/atlas/block.png-atlas")))
//        Minecraft.getInstance().blockRenderer
//        val buffer = bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.withDefaultNamespace("textures/atlas/block.png")))
        NeoComputers.LOGGER.info(packedLight.toHexString())
        renderModel(poseStack.last(), bufferSource, model!!.getQuads(ent.blockState, null, RandomSource.create()), packedLight)

//        renderer.renderModel(poseStack.last(), bufferSource, ent.blockState, model!!, 1f, 1f, 1f, 0xF000F0, packedOverlay)

        poseStack.popPose()
    }

    fun renderModel(pose: PoseStack.Pose, bufferSource: MultiBufferSource, quads: List<BakedQuad>, light: Int) {
        for (quad in quads) {
            val data = quad.vertices
            val buffer = bufferSource.getBuffer(RenderType.entitySolid(ResourceLocation.fromNamespaceAndPath(
                NeoComputers.MODID, "textures/block/robot.png"))) // TODO: use atlas tex instead of ts, also slanted normals
            for (i in 0..3) {
                val offset = i*8
                val fu = quad.sprite.u1 - quad.sprite.u0
                val fv = quad.sprite.v1 - quad.sprite.v0


                val x = Float.fromBits(data[offset+0])
                val y = Float.fromBits(data[offset+1])
                val z = Float.fromBits(data[offset+2])
                val col = data[offset+3]
                val u = Float.fromBits(data[offset+4])/fu
                val v = Float.fromBits(data[offset+5])/fv

                buffer.addVertex(pose, x, y, z).setColor(col).setUv(u, v).setLight(light).setNormal(quad.direction.stepX.toFloat(),quad.direction.stepY.toFloat(),quad.direction.stepZ.toFloat()).setOverlay(
                    OverlayTexture.NO_OVERLAY)
//                buffer.addVertex(pose, x, y, z).setUv(u, v)
            }
//            Minecraft.getInstance().blockRenderer.
        }
    }

}