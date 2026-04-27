package org.neoflock.neocomputers.platforms.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import org.neoflock.neocomputers.NeoComputers;
import org.neoflock.neocomputers.block.Blocks;
import org.neoflock.neocomputers.block.CableBlock;
import org.neoflock.neocomputers.entity.BlockEntities;
import org.neoflock.neocomputers.entity.render.CaseEntityRenderer;
import org.neoflock.neocomputers.entity.render.ScreenEntityRenderer;
import org.neoflock.neocomputers.item.Items;
import org.neoflock.neocomputers.platforms.fabric.client.model.ModelLoader;

public class NeoComputersFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new ModelLoader());
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getSCREEN_ENTITY().get(), ScreenEntityRenderer::new);
        BlockEntityRenderers.register(BlockEntities.INSTANCE.getCASE_ENTITY().get(), CaseEntityRenderer::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> {
            if (index == 0) {
                return state.getValue(CableBlock.Companion.getCOLOR()).getTextureDiffuseColor();
            } else {
                return 0xFFFFFF;
            }
        }, Blocks.INSTANCE.getCABLE_BLOCK().get());

        ColorProviderRegistry.ITEM.register((stack, index)-> {
            return DyeColor.LIGHT_GRAY.getTextureDiffuseColor();
        }, Items.INSTANCE.getITEMS().getRegistrar().get(ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "cable")));
    }
}
