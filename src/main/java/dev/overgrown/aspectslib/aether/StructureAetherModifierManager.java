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

public class StructureAetherModifierManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final Map<Identifier, AetherModifier> MODIFIER_MAP = new HashMap<>();

    public StructureAetherModifierManager() {
        super(GSON, "aether_densities/structure");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        MODIFIER_MAP.clear();
        prepared.forEach((resourceId, json) -> {
            try {
                String path = resourceId.getPath();
                // Extract structure ID directly from the resource path
                String structurePath = path.replace("aether_densities/structure/", "");
                if (structurePath.isEmpty()) {
                    AspectsLib.LOGGER.warn("Empty structure path in resource: {}", resourceId);
                    return;
                }

                // Remove .json if present
                if (structurePath.endsWith(".json")) {
                    structurePath = structurePath.substring(0, structurePath.length() - 5);
                }

                Identifier structureId = new Identifier(resourceId.getNamespace(), structurePath);
                JsonObject jsonObj = json.getAsJsonObject();
                AetherModifier modifier = AetherModifier.fromJson(jsonObj);
                MODIFIER_MAP.put(structureId, modifier);
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Error loading structure modifier: {}", e.getMessage());
            }
        });
        AspectsLib.LOGGER.info("Loaded {} structure aether modifiers", MODIFIER_MAP.size());
    }

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("structure_aether_modifier");
    }
}