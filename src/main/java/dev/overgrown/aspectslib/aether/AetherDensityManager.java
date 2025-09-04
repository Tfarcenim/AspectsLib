package dev.overgrown.aspectslib.aether;

import dev.overgrown.aspectslib.AspectsLib;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.Structure;

import java.util.HashMap;
import java.util.Map;

public class AetherDensityManager {
    public static AetherDensity getDensity(World world, BlockPos pos) {
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = null;

        if (biomeEntry.getKey().isPresent()) {
            biomeId = biomeEntry.getKey().get().getValue();
        } else if (world instanceof ServerWorld serverWorld) {
            biomeId = serverWorld.getRegistryManager()
                    .get(RegistryKeys.BIOME)
                    .getId(biomeEntry.value());
        }

        AspectsLib.LOGGER.debug("Getting density for biome: {} at position {}", biomeId, pos);
        AspectsLib.LOGGER.debug("Available biome densities: {}", BiomeAetherDensityManager.DENSITY_MAP.keySet());
        
        AetherDensity density = biomeId != null ?
                BiomeAetherDensityManager.DENSITY_MAP.getOrDefault(biomeId, AetherDensity.EMPTY) :
                AetherDensity.EMPTY;
                
        if (density == AetherDensity.EMPTY && biomeId != null) {
            AspectsLib.LOGGER.debug("No base density found for biome: {}", biomeId);
        } else if (density != AetherDensity.EMPTY) {
            AspectsLib.LOGGER.debug("Found base density for biome {}: {}", biomeId, density.getDensities());
        }
        if (world instanceof ServerWorld serverWorld) {
            Chunk chunk = world.getChunk(pos);
            Map<Structure, StructureStart> structureStarts = chunk.getStructureStarts();

            Map<Identifier, Double> additiveModifiers = new HashMap<>();
            Map<Identifier, Double> multiplicativeModifiers = new HashMap<>();

            for (var entry : structureStarts.entrySet()) {
                Structure structure = entry.getKey();
                StructureStart start = entry.getValue();

                if (start != null && start.getBoundingBox() != null && start.getBoundingBox().contains(pos)) {
                    Identifier structureId = world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure);
                    if (structureId == null) continue;

                    AetherModifier modifier = StructureAetherModifierManager.MODIFIER_MAP.get(structureId);
                    if (modifier == null) continue;

                    for (var aspectEntry : modifier.modifiers().entrySet()) {
                        Identifier aspect = aspectEntry.getKey();
                        double value = aspectEntry.getValue();

                        switch (modifier.operation()) {
                            case ADD -> additiveModifiers.merge(aspect, value, Double::sum);
                            case MULTIPLY -> multiplicativeModifiers.merge(aspect,
                                    multiplicativeModifiers.getOrDefault(aspect, 1.0) * value,
                                    (a, b) -> a * b
                            );
                        }
                    }
                }
            }

            Map<Identifier, Double> finalDensities = new HashMap<>(density.getDensities());

            additiveModifiers.forEach((aspect, value) ->
                    finalDensities.merge(aspect, value, Double::sum)
            );

            multiplicativeModifiers.forEach((aspect, value) ->
                    finalDensities.computeIfPresent(aspect, (k, v) -> v * value)
            );

            if (biomeId != null) {
                Map<Identifier, Double> dynamicMods = DynamicAetherDensityManager.getModifications(biomeId);
                if (dynamicMods != null) {
                    for (Map.Entry<Identifier, Double> entry : dynamicMods.entrySet()) {
                        finalDensities.merge(entry.getKey(), entry.getValue(), Double::sum);
                    }
                }
            }

            return new AetherDensity(finalDensities);
        }

        return density;
    }
}