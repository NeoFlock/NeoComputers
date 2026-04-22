package org.neoflock.neocomputers.gui.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.ChestRenderer
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.EnumProperty
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.NeoComputers.BlockEntityRenderType
import org.neoflock.neocomputers.block.ScreenBlock
import org.neoflock.neocomputers.entity.ScreenEntity

class ScreenEntityRenderer(val context: BlockEntityRendererProvider.Context?) : BlockEntityRenderer<ScreenEntity> {

    override fun render(entity: ScreenEntity, partialTick: Float, mat: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        mat.pushPose()
        handleDirection(entity, mat)
        mat.translate(2 / 16f, 2 / 16f, 0.0001f) // am i epstein or am i just retarded
//        mat.mulPose(Axis.YP.rotationDegrees(180f))
//        handleDirection(entity, mat)

        //        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, entity.bound))

        val buffer = bufferSource.getBuffer(BlockEntityRenderType) // idk the correct rendertype for ts
        buffer.addVertex(mat.last(), 3 / 4f, 0f, 0f).setUv(1f, 1f)
        buffer.addVertex(mat.last(), 3 / 4f, 3 / 4f, 0f).setUv(1f, 0f)
        buffer.addVertex(mat.last(), 0f, 3 / 4f, 0f).setUv(0f, 0f)
        buffer.addVertex(mat.last(), 0f, 0f, 0f).setUv(0f, 1f)
        mat.popPose()
    }

    private fun handleDirection(ent: ScreenEntity, mat: PoseStack) { // TODO: separate up and down from cardinal directions
        when (ent.blockState.getValue(ScreenBlock.FACING)) {
            Direction.SOUTH -> { mat.translate(0F, 0F, 1F) }
            Direction.EAST -> { mat.mulPose(Axis.YP.rotationDegrees(90F)); mat.translate(-1F, 0F, 1F) }
            Direction.WEST -> { mat.mulPose(Axis.YN.rotationDegrees(90F)); }
            Direction.NORTH -> {mat.mulPose(Axis.YP.rotationDegrees(180F)); mat.translate(-1F, 0F, 0F) }
            Direction.UP -> { mat.mulPose(Axis.XN.rotationDegrees(90F)); mat.mulPose(Axis.ZP.rotationDegrees(180F)); mat.translate(-1.0001F, 0F, 1F) } // idek
            Direction.DOWN -> { mat.mulPose(Axis.XP.rotationDegrees(90F)); mat.mulPose(Axis.ZN.rotationDegrees(180F)); mat.translate(-1F, -1F, 0F) }
            else -> return
        }
    }
//    private fun handleDirection(ent: ScreenEntity, mat: PoseStack?) {
//        `when`(ent.getBlockState().get)
//    }
}