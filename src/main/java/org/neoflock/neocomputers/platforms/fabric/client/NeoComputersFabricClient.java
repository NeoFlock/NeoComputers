package org.neoflock.neocomputers.platforms.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.neoflock.neocomputers.entity.BlockEntities;
import org.neoflock.neocomputers.entity.render.CaseEntityRenderer;
import org.neoflock.neocomputers.entity.render.ScreenEntityRenderer;
import org.neoflock.neocomputers.platforms.fabric.client.model.ModelLoader;

public class NeoComputersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new ModelLoader());
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getSCREEN_ENTITY().get(), ScreenEntityRenderer::new);
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getCASE_ENTITY().get(), CaseEntityRenderer::new);
    }
}
