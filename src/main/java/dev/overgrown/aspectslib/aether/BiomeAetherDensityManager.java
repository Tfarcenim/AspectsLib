package dev.overgrown.aspectslib.aether;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.overgrown.aspectslib.AspectsLib;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class BiomeAetherDensityManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final Map<Identifier, AetherDensity> DENSITY_MAP = new HashMap<>();

    public BiomeAetherDensityManager() {
        super(GSON, "aether_densities/biome");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        DENSITY_MAP.clear();
        prepared.forEach((resourceId, json) -> {
            try {
                String path = resourceId.getPath();
                String biomePath = path.replace("aether_densities/biome/", "");
                if (biomePath.isEmpty()) {
                    AspectsLib.LOGGER.warn("Empty biome path in resource: {}", resourceId);
                    return;
                }

                if (biomePath.endsWith(".json")) {
                    biomePath = biomePath.substring(0, biomePath.length() - 5);
                }

                Identifier biomeId = new Identifier(resourceId.getNamespace(), biomePath);
                JsonObject jsonObj = json.getAsJsonObject();

                // Handle different JSON structures
                JsonObject valuesObj = null;
                if (jsonObj.has("values")) {
                    valuesObj = jsonObj.getAsJsonObject("values");
                } else {
                    // Fallback: treat the entire object as values
                    valuesObj = jsonObj;
                }

                AetherDensity density = AetherDensity.fromJson(valuesObj);
                DENSITY_MAP.put(biomeId, density);
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Error loading biome aether density from {}: {}", resourceId, e.getMessage());
            }
        });
        AspectsLib.LOGGER.info("Loaded {} biome aether densities", DENSITY_MAP.size());
    }

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("biome_aether_density");
    }
}