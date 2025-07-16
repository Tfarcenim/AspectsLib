package dev.overgrown.aspectslib;

import dev.overgrown.aspectslib.aether.AetherManager;
import dev.overgrown.aspectslib.data.AspectManager;
import dev.overgrown.aspectslib.data.CustomItemTagManager;
import dev.overgrown.aspectslib.resonance.ResonanceManager;
import dev.overgrown.aspectslib.networking.SyncAspectIdentifierPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the AspectsLib mod.
 * <p>
 * Responsibilities:
 * <ol type="1">
 *     <li>Initializes core systems</li>
 *     <li>Registers resource reload listeners</li>
 *     <li>Sets up networking for aspect synchronization</li>
 * </ol>
 * </p>
 * <p>
 * Usage:
 * <li>This class is automatically initialized by Fabric</li>
 * <li>Other mods can access library features through {@link dev.overgrown.aspectslib.api.AspectsAPI}</li>
 * </p>
 * <br>
 * <p>
 * Important Connections:
 * <li>{@link AspectManager}: Manages aspect data loading</li>
 * <li>{@link CustomItemTagManager}: Handles item-aspect associations</li>
 * <li>{@link SyncAspectIdentifierPacket}: Synchronizes aspect data to clients</li>
 * </p>
 */
public class AspectsLib implements ModInitializer {
	public static final String MOD_ID = "aspectslib";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Helper for creating namespaced identifiers */
	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Sync aspect data to players when they join
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			try {
				SyncAspectIdentifierPacket.sendAllData(player);
				AspectsLib.LOGGER.debug("Sent aspect data to player: {}", player.getName().getString());
			} catch (Exception e) {
				AspectsLib.LOGGER.error("Failed to send aspect data to player {}: {}",
						player.getName().getString(), e.getMessage());
			}
		});

		// Initialize and register data managers
		AspectManager aspectManager = new AspectManager();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(aspectManager);
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CustomItemTagManager());

		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new ResonanceManager());

		// Register Aether Manager
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new AetherManager());

		LOGGER.info("AspectsLib initialized!");
	}
}