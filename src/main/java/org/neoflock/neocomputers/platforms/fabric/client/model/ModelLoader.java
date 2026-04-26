package org.neoflock.neocomputers.platforms.fabric.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.neoflock.neocomputers.NeoComputers;

public class ModelLoader implements ModelLoadingPlugin {
    public static final ResourceLocation CABLE = ResourceLocation.fromNamespaceAndPath(NeoComputers.MODID, "cable");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        pluginContext.modifyModelOnLoad().register((original, context) -> {
//           final ModelResourceLocation id = context.topLevelId();
////           if (id != null && id.id().getNamespace().equals(NeoComputers.MODID)) NeoComputers.INSTANCE.getLOGGER().info("{} {} {}", id.id().getNamespace(), id.id().getPath(), id.id().getPath().equals(CABLE.id().getPath()));
//           if (id != null && id.id().equals(CABLE)) {
////               NeoComputers.INSTANCE.getLOGGER().error("DOING CABLEEEEEEE");
//               return new CableModel();
//           } else {
//               return original;
//           }
            return original;
        });
    }
}
