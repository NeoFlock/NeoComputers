package org.neoflock.neocomputers.platforms.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.neoflock.neocomputers.entity.BlockEntities;
import org.neoflock.neocomputers.gui.render.CaseEntityRenderer;
import org.neoflock.neocomputers.gui.render.ScreenEntityRenderer;

public class NeoComputersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getSCREEN_ENTITY().get(), ScreenEntityRenderer::new);
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getCASE_ENTITY().get(), CaseEntityRenderer::new);
    }
}
