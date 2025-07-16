package dev.overgrown.aspectslib.aether;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.overgrown.aspectslib.AspectsLib;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.structure.Structure;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AetherManager implements IdentifiableResourceReloadListener {
    public static final double DEAD_ZONE_CHANCE = 0.001; // 0.1% chance
    public static final Map<Identifier, Map<Identifier, Double>> BIOME_DENSITIES = new HashMap<>();
    public static final Map<Identifier, Map<Identifier, Double>> STRUCTURE_DENSITIES = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("aether_manager");
    }

    @Override
    public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager,
                                          Profiler prepareProfiler, Profiler applyProfiler,
                                          Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.runAsync(() -> {
            loadBiomeDensities(manager);
            loadStructureDensities(manager);
        }, prepareExecutor);
    }

    public static void initializeChunkAetherData(ServerWorld world, Chunk chunk) {
        ChunkAetherData data = new ChunkAetherData();
        ChunkPos pos = chunk.getPos();

        Random random = new Random(world.getSeed() + pos.toLong());

        if (random.nextDouble() < DEAD_ZONE_CHANCE) {
            data.setPermanentDeadZone(true);
        } else {
            applyBiomeDensity(world, chunk, data);
            applyStructureDensity(world, chunk, data);
        }

        // Fixed: Cast to WorldChunk and use setAetherData method from mixin
        if (chunk instanceof WorldChunk worldChunk) {
            ((AetherDataHolder) worldChunk).setAetherData(data);
        }
    }

    private static void applyBiomeDensity(ServerWorld world, Chunk chunk, ChunkAetherData data) {
        Registry<Biome> biomeRegistry = world.getRegistryManager().get(RegistryKeys.BIOME);
        BlockPos centerPos = chunk.getPos().getCenterAtY(0);
        Biome biome = world.getBiome(centerPos).value();
        Identifier biomeId = biomeRegistry.getId(biome);

        if (biomeId != null && BIOME_DENSITIES.containsKey(biomeId)) {
            for (Map.Entry<Identifier, Double> entry : BIOME_DENSITIES.get(biomeId).entrySet()) {
                data.setDensity(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void applyStructureDensity(ServerWorld world, Chunk chunk, ChunkAetherData data) {
        Registry<Structure> structureRegistry = world.getRegistryManager().get(RegistryKeys.STRUCTURE);

        for (Structure structure : chunk.getStructureStarts().keySet()) {
            Identifier structureId = structureRegistry.getId(structure);

            if (structureId != null && STRUCTURE_DENSITIES.containsKey(structureId)) {
                for (Map.Entry<Identifier, Double> entry : STRUCTURE_DENSITIES.get(structureId).entrySet()) {
                    double current = data.getDensity(entry.getKey());
                    data.setDensity(entry.getKey(), current + entry.getValue());
                }
            }
        }
    }

    private void loadBiomeDensities(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources("aether_density/biome", path -> {
            return path.getPath().endsWith(".json");
        });

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream));

                String path = entry.getKey().getPath();
                String biomeId = path.substring("aether_density/biome/".length(), path.length() - 5);

                Map<Identifier, Double> densities = new HashMap<>();

                for (Map.Entry<String, JsonElement> aspectEntry : json.entrySet()) {
                    Identifier aspectId = new Identifier(aspectEntry.getKey());
                    double density = aspectEntry.getValue().getAsDouble();
                    densities.put(aspectId, density);
                }

                BIOME_DENSITIES.put(new Identifier(biomeId), densities);
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Failed to load biome aether density: {}", entry.getKey(), e);
            }
        }
    }

    private void loadStructureDensities(ResourceManager manager) {
        Map<Identifier, Resource> resources = manager.findResources("aether_density/structure", path -> {
            // Fixed: Use getPath() on Identifier
            return path.getPath().endsWith(".json");
        });

        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().getInputStream()) {
                JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream));

                // Extract structure ID from path
                String path = entry.getKey().getPath();
                String structureId = path.substring("aether_density/structure/".length(), path.length() - 5);

                Map<Identifier, Double> densities = new HashMap<>();

                for (Map.Entry<String, JsonElement> aspectEntry : json.entrySet()) {
                    Identifier aspectId = new Identifier(aspectEntry.getKey());
                    double density = aspectEntry.getValue().getAsDouble();
                    densities.put(aspectId, density);
                }

                STRUCTURE_DENSITIES.put(new Identifier(structureId), densities);
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Failed to load structure aether density: {}", entry.getKey(), e);
            }
        }
    }
}