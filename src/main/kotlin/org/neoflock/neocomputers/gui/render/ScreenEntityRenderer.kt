package org.neoflock.neocomputers.gui.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.ChestRenderer
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.EnumProperty
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.ScreenBlock
import org.neoflock.neocomputers.entity.ScreenEntity


class ScreenEntityRenderer(val context: BlockEntityRendererProvider.Context?) : BlockEntityRenderer<ScreenEntity> { // TODO: FORGE

    companion object {
        val RENDER_TYPE: (ResourceLocation) -> RenderType = { t: ResourceLocation ->
            RenderType.create(
        "nc_screen",
        DefaultVertexFormat.POSITION_TEX,
        VertexFormat.Mode.QUADS,
        RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder().
                setShaderState(RenderStateShard.POSITION_TEX_SHADER).
                setTextureState(RenderStateShard.TextureStateShard(t, false, false))
                    .createCompositeState(false))
        }
    }

//    var renderer: ChestRenderer
    override fun render(entity: ScreenEntity, partialTick: Float, mat: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        var facing = entity.blockState.getValue(ScreenBlock.FACING)

        var nx = if(facing==Direction.EAST) 1F else if (facing==Direction.WEST) -1F else 0F
        var ny = if(facing==Direction.UP) 1F else if (facing==Direction.DOWN) -1F else 0F
        var nz = if(facing==Direction.SOUTH) 1F else if (facing==Direction.EAST) -1F else 0F


        mat.pushPose()
        handleDirection(facing, mat)
        mat.translate(2 / 16f, 2 / 16f, 0.0001f) // am i epstein or am i just retarded

//        val rendertype = RENDER_TYPE(entity.node.address.toString(), ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, entity.bound))
        val rendertype = RENDER_TYPE(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, entity.bound))
        val buffer = bufferSource.getBuffer(rendertype)
        buffer.addVertex(mat.last(), 3 / 4f, 0f, 0f).setUv(1f, 1f)
        buffer.addVertex(mat.last(), 3 / 4f, 3 / 4f, 0f).setUv(1f, 0f)
        buffer.addVertex(mat.last(), 0f, 3 / 4f, 0f).setUv(0f, 0f)
        buffer.addVertex(mat.last(), 0f, 0f, 0f).setUv(0f, 1f)
        mat.popPose()
    }

    private fun addCommonSlop(vert: VertexConsumer, entity: ScreenEntity) {
//        vert.setUv2(15, 15).setColor(255, 255, 255, 255).setNormal()
    }

    private fun handleDirection(facing: Direction, mat: PoseStack) { // TODO: separate up and down from cardinal directions
        when (facing) {
            Direction.SOUTH -> { mat.translate(0F, 0F, 1F) }
            Direction.EAST -> { mat.mulPose(Axis.YP.rotationDegrees(90F)); mat.translate(-1F, 0F, 1F) }
            Direction.WEST -> { mat.mulPose(Axis.YN.rotationDegrees(90F)); }
            Direction.NORTH -> {mat.mulPose(Axis.YP.rotationDegrees(180F)); mat.translate(-1F, 0F, 0F) }
            Direction.UP -> { mat.mulPose(Axis.XN.rotationDegrees(90F)); mat.mulPose(Axis.ZP.rotationDegrees(180F)); mat.translate(-1F, 0F, 1F) } // idek
            Direction.DOWN -> { mat.mulPose(Axis.XP.rotationDegrees(90F)); mat.mulPose(Axis.ZN.rotationDegrees(180F)); mat.translate(-1F, -1F, 0F) }
        }
    }
//    private fun handleDirection(ent: ScreenEntity, mat: PoseStack?) {
//        `when`(ent.getBlockState().get)
//    }
}