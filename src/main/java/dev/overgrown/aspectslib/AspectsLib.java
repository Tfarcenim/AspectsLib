package dev.overgrown.aspectslib;

import dev.overgrown.aspectslib.aether.BiomeAetherDensityManager;
import dev.overgrown.aspectslib.aether.CorruptionManager;
import dev.overgrown.aspectslib.aether.StructureAetherModifierManager;
import dev.overgrown.aspectslib.command.AetherDensityCommand;
import dev.overgrown.aspectslib.data.AspectManager;
import dev.overgrown.aspectslib.data.CustomItemTagManager;
import dev.overgrown.aspectslib.data.EntityAspectManager;
import dev.overgrown.aspectslib.entity.aura_node.AuraNodeEntity;
import dev.overgrown.aspectslib.registry.ModEntities;
import dev.overgrown.aspectslib.registry.ModItems;
import dev.overgrown.aspectslib.resonance.ResonanceManager;
import dev.overgrown.aspectslib.networking.SyncAspectIdentifierPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
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

	public static final TagKey<Biome> SPAWNS = TagKey.of(RegistryKeys.BIOME,identifier("spawns"));

	/** Helper for creating namespaced identifiers */
	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
        ModItems.initialize();
		ModEntities.register();

		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			AetherDensityCommand.register(dispatcher);
		});

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

		// Register entity aspect manager
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new EntityAspectManager());

		// Register Aether density loaders
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new BiomeAetherDensityManager());
		ResourceManagerHelper.get(ResourceType.SERVER_DATA)
				.registerReloadListener(new StructureAetherModifierManager());

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                CorruptionManager.tick(world);
            }
        });


		BiomeModifications.addSpawn(biome -> biome.getBiomeRegistryEntry().isIn(SPAWNS), SpawnGroup.MONSTER,
				ModEntities.AURA_NODE, 1000, 1, 1);
		SpawnRestriction.RESTRICTIONS.put(ModEntities.AURA_NODE,new SpawnRestriction.Entry(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,//does this need a custom enum?
                SpawnRestriction.Location.NO_RESTRICTIONS, (type, world, spawnReason, pos, random) -> AuraNodeEntity.isValidNaturalSpawn(
                                (EntityType<? extends AuraNodeEntity>)(Object) type, world, spawnReason, pos, random)));

		LOGGER.info("AspectsLib initialized!");
	}
}