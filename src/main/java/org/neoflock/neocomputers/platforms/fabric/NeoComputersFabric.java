//? if fabric {
package org.neoflock.neocomputers.platforms.fabric;

import org.neoflock.neocomputers.ModPlatform;
import net.fabricmc.api.ModInitializer;
import org.neoflock.neocomputers.NeoComputers;
import net.fabricmc.loader.api.FabricLoader;

public class NeoComputersFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		NeoComputers.INSTANCE.entrypoint(new FabricPlatform());
	}
	public static class FabricPlatform implements ModPlatform{

		@Override
		public String getModloader() {
			return "Fabric";
		}

		@Override
		public boolean isModLoaded(String modloader) {
			return FabricLoader.getInstance().isModLoaded(modloader);
		}
	}
}
//?}