//? if forge {
/*package org.neoflock.neocomputers.neocomputers.platforms.forge;

import org.neoflock.neocomputers.ConfigScreen;
import org.neoflock.neocomputers.NeoComputersInit;
import org.neoflock.neocomputers.ModPlatform;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.neoflock.neocomputers.NeoComputers;

@Mod("neocomputers")
public class NeoComputersForge {
	public NeoComputersForge() {
		NeoComputers.INSTANCE.entrypoint(new ForgePlatform());
        MinecraftForge.registerConfigScreen(ConfigScreen::createConfigScreen);
	}
	public static class ForgePlatform implements ModPlatform {
		@Override
		public String getModloader() {
			return "LexForge";
		}

		@Override
		public boolean isModLoaded(String modId) {
			return ModList.get().isLoaded(modId);
		}
	}

}
*///?}