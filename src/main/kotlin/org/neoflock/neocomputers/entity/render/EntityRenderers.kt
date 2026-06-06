package org.neoflock.neocomputers.entity.render

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import org.neoflock.neocomputers.entity.BlockEntities

object EntityRenderers {
    fun registerBlockEntityRenderers() {
        BlockEntityRenderers.register(BlockEntities.SCREEN_ENTITY.get(), ::ScreenEntityRenderer); // TODO: put this in common
        BlockEntityRenderers.register(BlockEntities.CASE_ENTITY.get(), ::CaseEntityRenderer);
        BlockEntityRenderers.register(BlockEntities.RELAY_ENTITY.get(), ::RelayEntityRenderer);
        BlockEntityRenderers.register(BlockEntities.ROBOT_ENTITY.get(), ::RobotEntityRenderer);
        BlockEntityRenderers.register(BlockEntities.RACK_ENTITY.get(), ::RackEntityRenderer);
        BlockEntityRenderers.register(BlockEntities.ASSEMBLER_ENTITY.get(), ::AssemblerEntityRenderer)
    }
}