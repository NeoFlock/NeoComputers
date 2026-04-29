package org.neoflock.neocomputers.entity.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.RelayEntity
import kotlin.math.min

class RelayEntityRenderer(val context: BlockEntityRendererProvider.Context?): BlockEntityRenderer<RelayEntity> {
    val RELAY_ON_TEX = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/relay_side_on.png")

    val RENDER_TYPE: RenderType =
        RenderType.create(
            "nc_screen",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder().
            setShaderState(RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader)).
            setTextureState(RenderStateShard.TextureStateShard(RELAY_ON_TEX, false, false))
                .createCompositeState(false))

    override fun render(
        blockEntity: RelayEntity,
        partialTick: Float,
        mat: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if(blockEntity.activityTickLeft == 0) return

        val alpha = min((blockEntity.activityTickLeft.toFloat() * 255 / 20).toInt(), 255)

        for(i in 0..<4) {
            mat.pushPose()

            val antiZFight = 0.001F

            mat.rotateAround(Axis.YN.rotationDegrees(90F * i), 0.5F, 0.5F, 0.5F)
            mat.translate(0F, 0F, 1F + antiZFight)

            val width = 1F
            val height = 1F
            val bx = 0F
            val by = 0F

            val buffer = bufferSource.getBuffer(RENDER_TYPE)
            buffer.addVertex(mat.last(), bx + width, by, 0f).setUv(1f, 1f).setColor(alpha, alpha, alpha, alpha)
            buffer.addVertex(mat.last(), bx + width, by + height, 0f).setUv(1f, 0f).setColor(alpha, alpha, alpha, alpha)
            buffer.addVertex(mat.last(), bx, by + height, 0f).setUv(0f, 0f).setColor(alpha, alpha, alpha, alpha)
            buffer.addVertex(mat.last(), bx, by, 0f).setUv(0f, 1f).setColor(alpha, alpha, alpha, alpha)

            mat.popPose()
        }
    }
}