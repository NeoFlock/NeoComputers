package org.neoflock.neocomputers.mixin;

import org.neoflock.neocomputers.NeoComputers;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class ExampleMixin {

    @Inject(method = "init",at=@At("HEAD"))
    void init(CallbackInfo ci){
        NeoComputers.INSTANCE.getLOGGER().info("Stonecutter example mixin init in %s".formatted(NeoComputers.INSTANCE.getPLATFORM().getModloader()));
    }

}
