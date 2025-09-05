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
        AspectsLib.LOGGER.info("Starting to load biome aether densities from {} files", prepared.size());
        
        prepared.forEach((resourceId, json) -> {
            try {
                AspectsLib.LOGGER.debug("Processing resource: {}", resourceId);
                
                String path = resourceId.getPath();
                
                if (path.endsWith(".json")) {
                    path = path.substring(0, path.length() - 5);
                }
                
                Identifier biomeId;
                
                if (path.contains("/")) {
                    String[] parts = path.split("/");
                    if (parts.length >= 2) {
                        String namespace = parts[0];
                        String biomeName = String.join("/", java.util.Arrays.copyOfRange(parts, 1, parts.length));
                        biomeId = new Identifier(namespace, biomeName);
                    } else {
                        biomeId = new Identifier(resourceId.getNamespace(), path);
                    }
                } else {
                    biomeId = new Identifier(resourceId.getNamespace(), path);
                }
                
                AspectsLib.LOGGER.info("Loading aether density for biome: {}", biomeId);
                
                JsonObject jsonObj = json.getAsJsonObject();

                JsonObject valuesObj = null;
                if (jsonObj.has("values")) {
                    valuesObj = jsonObj.getAsJsonObject("values");
                } else {
                    valuesObj = jsonObj;
                }

                AetherDensity density = AetherDensity.fromJson(valuesObj);
                DENSITY_MAP.put(biomeId, density);
                
                AspectsLib.LOGGER.info("Successfully loaded {} aspects for biome {}: {}", 
                    density.getDensities().size(), biomeId, density.getDensities());
                    
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Error loading biome aether density from {}: {}", resourceId, e.getMessage(), e);
            }
        });
        
        AspectsLib.LOGGER.info("Completed loading {} biome aether densities", DENSITY_MAP.size());
        
        DENSITY_MAP.forEach((biomeId, density) -> {
            AspectsLib.LOGGER.debug("Biome {} has densities: {}", biomeId, density.getDensities());
        });
    }

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("biome_aether_density");
    }
}