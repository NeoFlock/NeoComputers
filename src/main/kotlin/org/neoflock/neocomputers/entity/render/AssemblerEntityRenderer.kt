package org.neoflock.neocomputers.entity.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.entity.AssemblerEntity

class AssemblerEntityRenderer(val context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<AssemblerEntity> {
    val RENDER_TYPE = {l: ResourceLocation ->
        RenderType.create("nc_assembler", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder()
            .setTextureState(RenderStateShard.TextureStateShard(l, false, false))
            .setShaderState(RenderStateShard.ShaderStateShard.POSITION_TEX_SHADER)
            .createCompositeState(false))
    }

    override fun render(blockEntity: AssemblerEntity, partialTick: Float, poseStack: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        var buffer = bufferSource.getBuffer(RENDER_TYPE(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/assembler_top_on.png")))
        val pose = poseStack.last()
        buffer.addVertex(pose, 0f, 17/16f, 1f).setUv(1f, 0f)
        buffer.addVertex(pose, 1f, 17/16f, 1f).setUv(1f, 1f)
        buffer.addVertex(pose, 1f, 17/16f, 0f).setUv(0f, 1f)
        buffer.addVertex(pose, 0f, 17/16f, 0f).setUv(0f, 0f)

        // TODO: do assembling texture
        buffer = bufferSource.getBuffer(RENDER_TYPE(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/assembler_side_on.png")))
        poseStack.pushPose()
        for (i in 0..3) {
            val pose = poseStack.last()
            buffer.addVertex(pose, 1.001f, 1f, 0f).setUv(0f, 0f)
            buffer.addVertex(pose, 1.001f, 1f, 1f).setUv(1f, 0f)
            buffer.addVertex(pose, 1.001f, 0f, 1f).setUv(1f, 1f)
            buffer.addVertex(pose, 1.001f, 0f, 0f).setUv(0f, 1f)
            poseStack.rotateAround(Axis.YP.rotationDegrees(90f), 0.5f, 0.5f, 0.5f)
        }
        poseStack.popPose()

    }
}