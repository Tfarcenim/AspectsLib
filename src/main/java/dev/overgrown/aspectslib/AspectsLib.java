package dev.overgrown.aspectslib;

import dev.overgrown.aspectslib.data.AspectManager;
import dev.overgrown.aspectslib.data.CustomItemTagManager;
import dev.overgrown.aspectslib.networking.SyncAspectIdentifierPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AspectsLib implements ModInitializer {
	public static final String MOD_ID = "aspectslib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			try {
				SyncAspectIdentifierPacket.sendAllData(player);
				AspectsLib.LOGGER.debug("Sent aspect data to player: {}", player.getName().getString());
			} catch (Exception e) {
				AspectsLib.LOGGER.error("Failed to send aspect data to player {}: {}", player.getName().getString(), e.getMessage());
			}
		});


        AspectManager aspectManager = new AspectManager();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(aspectManager);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CustomItemTagManager());

		LOGGER.info("AspectsLib initialized!");
	}
}