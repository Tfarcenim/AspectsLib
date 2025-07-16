package dev.overgrown.aspectslib.data;

import com.google.gson.*;
import dev.overgrown.aspectslib.AspectsLib;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityAspectManager extends JsonDataLoader implements IdentifiableResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public EntityAspectManager() {
        super(GSON, "entity_aspects");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        EntityAspectRegistry.clear();

        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            Identifier fileId = entry.getKey();
            JsonObject json = entry.getValue().getAsJsonObject();

            try {
                Identifier entityId = new Identifier(json.get("entity").getAsString());
                JsonObject aspectsObj = json.getAsJsonObject("aspects");

                Object2IntOpenHashMap<Identifier> aspects = new Object2IntOpenHashMap<>();
                for (Map.Entry<String, JsonElement> aspectEntry : aspectsObj.entrySet()) {
                    Identifier aspectId = new Identifier(aspectEntry.getKey());
                    int amount = aspectEntry.getValue().getAsInt();
                    aspects.put(aspectId, amount);
                }

                EntityAspectRegistry.register(entityId, new AspectData(aspects));
            } catch (Exception e) {
                AspectsLib.LOGGER.error("Error loading entity aspects from {}: {}", fileId, e.getMessage());
            }
        }
        AspectsLib.LOGGER.info("Loaded {} entity aspect mappings", prepared.size());
    }

    @Override
    public Identifier getFabricId() {
        return AspectsLib.identifier("entity_aspects");
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return List.of(AspectsLib.identifier("aspects"));
    }
}