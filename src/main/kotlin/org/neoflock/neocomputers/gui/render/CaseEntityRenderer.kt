package org.neoflock.neocomputers.gui.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import org.neoflock.neocomputers.NeoComputers
import org.neoflock.neocomputers.block.CaseBlock
import org.neoflock.neocomputers.entity.CaseBlockEntity

class CaseEntityRenderer(private val context: BlockEntityRendererProvider.Context?) : BlockEntityRenderer<CaseBlockEntity>{

    val OFF = 0xFF5F855E.toInt()
    val GREEN = 0xFF4EDC5E.toInt()
    val RED = 0xFFff102B.toInt()

    val BLINKTIME: Long = 10 // in ticks

    val RENDER_TYPE = RenderType.create("nc_case", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
        RenderType.TRANSIENT_BUFFER_SIZE, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .createCompositeState(false))

    override fun render(ent: CaseBlockEntity, partialTick: Float, mat: PoseStack, bufferSource: MultiBufferSource, packedLight: Int, packedOverlay: Int) {
        val buffer = bufferSource.getBuffer(RENDER_TYPE);

        mat.pushPose()
        handleDirection(ent.blockState.getValue(CaseBlock.FACING), mat)

        mat.translate(5/16F, 14/16F, 0.0001F)
        if (ent.isOn) drawLED(buffer, mat.last(), 3F)
        else if (ent.getLastError() != null) { // if else hell
            if ((ent.level!!.dayTime/BLINKTIME) % 2 == 1.toLong()) drawLED(buffer, mat.last(), 3F, RED)
            else drawLED(buffer, mat.last(), 3F, OFF)
        } else drawLED(buffer, mat.last(), 3F, OFF)

        mat.translate(6/16F, 0F, 0F)
        drawLED(buffer, mat.last(), 2F, if (ent.diskActivityTime > 0) GREEN else OFF)

        mat.popPose()

    }
    private fun drawLED(buffer: VertexConsumer, mat: PoseStack.Pose, width: Float, color: Int = GREEN) {
        buffer.addVertex(mat, width/16F, 0F, 0F).setColor(color)
        buffer.addVertex(mat, width/16F, 1/16F, 0F).setColor(color)
        buffer.addVertex(mat, 0F, 1/16F, 0F).setColor(color)
        buffer.addVertex(mat, 0F, 0F, 0F).setColor(color)
    }

    private fun handleDirection(facing: Direction, mat: PoseStack) {
        when (facing) {
            Direction.SOUTH -> { mat.translate(0F, 0F, 1F) }
            Direction.EAST -> { mat.mulPose(Axis.YP.rotationDegrees(90F)); mat.translate(-1F, 0F, 1F) }
            Direction.WEST -> { mat.mulPose(Axis.YN.rotationDegrees(90F)); }
            Direction.NORTH -> {mat.mulPose(Axis.YP.rotationDegrees(180F)); mat.translate(-1F, 0F, 0F) }
            else -> return
        }
    }
}
