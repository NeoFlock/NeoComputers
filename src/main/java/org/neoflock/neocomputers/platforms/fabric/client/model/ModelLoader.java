package org.neoflock.neocomputers.platforms.fabric.client.model;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.neoflock.neocomputers.NeoComputers;
import org.neoflock.neocomputers.block.model.RobotModel;

public class ModelLoader implements ModelLoadingPlugin {
    public static final ResourceLocation ROBOT = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "robot");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
           final ModelResourceLocation id = context.topLevelId();
           if (id != null && id.id().equals(ROBOT)) {
               return new FabricModelWrapper(new RobotModel());
           }
            return original;
        });
    }
}
