package org.neoflock.neocomputers.item

import com.mojang.blaze3d.shaders.Shader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.neoflock.neocomputers.NeoComputers

interface RackItem {
//    companion object {
//        val RENDER_TYPE = {l: ResourceLocation ->
//            RenderType.create("nc_server", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, RenderType.SMALL_BUFFER_SIZE, RenderType.CompositeState.builder()
//                .setShaderState(RenderStateShard.ShaderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
//                .setLightmapState(RenderStateShard.LIGHTMAP)
//                .setTextureState(RenderStateShard.TextureStateShard(l, false, false))
//                .createCompositeState(false))
//        }
//    }
    val TOP_TEX: ResourceLocation
        get() = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/generic_top.png")

    val FRONT_TEX: ResourceLocation
        get() = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "textures/block/rack_server.png")

    fun render(source: MultiBufferSource, stack: PoseStack, light: Int, v_offset: Float = 2f) {
        val pose = stack.last()


//        var buffer = source.getBuffer(RenderType.gui()) // TODO: correct rendertype
        var buffer = source.getBuffer(RenderType.entitySolid(TOP_TEX))
//        val u1 = 1/16f
        buffer.addVertex(pose, 0f, 0f, 0f).setUv(1/16f, 15/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, -1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 0f, 0f).setUv(1/16f, 1/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, -1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 0f, 14/16f).setUv(15/16f, 1/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, -1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 0f, 0f, 14/16f).setUv(15/16f, 1/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, -1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)

        buffer.addVertex(pose, 0f, 3/16f, 14/16f).setUv(15/16f, 1/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, 1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 3/16f, 14/16f).setUv(15/16f, 15/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, 1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 3/16f, 0f).setUv(1/16f, 15/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, 1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 0f, 3/16f, 0f).setUv(1/16f, 1/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 0f, 1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)

        buffer = source.getBuffer(RenderType.entitySolid(FRONT_TEX))
        buffer.addVertex(pose, 14/16f, 3/16f, 14/16f).setUv(1/16f, v_offset/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 1f, 0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 0f, 14/16f).setUv(1/16f, (v_offset+3)/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 1f, 0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 0/16f, 0/16f).setUv(15/16f, (v_offset+3)/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 1f, 0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buffer.addVertex(pose, 14/16f, 3/16f, 0/16f).setUv(15/16f, v_offset/16f).setColor(1f, 1f, 1f, 1f).setLight(light).setNormal(pose, 1f, 0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)

    }

}