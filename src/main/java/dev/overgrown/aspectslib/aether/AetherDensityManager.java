package dev.overgrown.aspectslib.aether;

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
        // Get base density from biome
        RegistryEntry<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = null;

        // Safely get biome ID
        if (biomeEntry.getKey().isPresent()) {
            biomeId = biomeEntry.getKey().get().getValue();
        } else if (world instanceof ServerWorld serverWorld) {
            biomeId = serverWorld.getRegistryManager()
                    .get(RegistryKeys.BIOME)
                    .getId(biomeEntry.value());
        }

        AetherDensity density = biomeId != null ?
                BiomeAetherDensityManager.DENSITY_MAP.getOrDefault(biomeId, AetherDensity.EMPTY) :
                AetherDensity.EMPTY;

        // Apply structure modifiers
        if (world instanceof ServerWorld serverWorld) {
            Chunk chunk = world.getChunk(pos);
            Map<Structure, StructureStart> structureStarts = chunk.getStructureStarts();

            Map<Identifier, Double> additiveModifiers = new HashMap<>();
            Map<Identifier, Double> multiplicativeModifiers = new HashMap<>();

            for (var entry : structureStarts.entrySet()) {
                Structure structure = entry.getKey();
                StructureStart start = entry.getValue();

                // Null-safe bounding box check
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

            // Apply modifiers
            Map<Identifier, Double> finalDensities = new HashMap<>(density.getDensities());

            // Additive modifiers
            additiveModifiers.forEach((aspect, value) ->
                    finalDensities.merge(aspect, value, Double::sum)
            );

            // Multiplicative modifiers
            multiplicativeModifiers.forEach((aspect, value) ->
                    finalDensities.computeIfPresent(aspect, (k, v) -> v * value)
            );

            // Apply dynamic modifications
            if (biomeId != null) {
                // Get dynamic modifications for this biome
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