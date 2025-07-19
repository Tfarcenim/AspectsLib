package dev.overgrown.aspectslib.aether;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AetherDensity {
    public static final AetherDensity EMPTY = new AetherDensity(Collections.emptyMap());
    private final Map<Identifier, Double> densities;

    public AetherDensity(Map<Identifier, Double> densities) {
        this.densities = Map.copyOf(densities);
    }

    public double getDensity(Identifier aspect) {
        return densities.getOrDefault(aspect, 0.0);
    }

    public Map<Identifier, Double> getDensities() {
        return Collections.unmodifiableMap(densities);
    }

    public static AetherDensity fromJson(JsonObject json) {
        Map<Identifier, Double> densityMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            Identifier aspectId = new Identifier(entry.getKey());
            double density = entry.getValue().getAsDouble();
            densityMap.put(aspectId, density);
        }
        return new AetherDensity(densityMap);
    }
}