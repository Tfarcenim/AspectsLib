package dev.overgrown.aspectslib.aether;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.overgrown.aspectslib.AspectsLib;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record AetherModifier(
        AetherModifierOperation operation,
        Map<Identifier, Double> modifiers
) {
    public static AetherModifier fromJson(JsonObject json) {
        // Get operation with fallback to ADD if missing
        AetherModifierOperation operation = AetherModifierOperation.ADD;
        if (json.has("operation")) {
            try {
                operation = AetherModifierOperation.valueOf(
                        json.get("operation").getAsString().toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                AspectsLib.LOGGER.warn("Invalid operation type: {}", json.get("operation").getAsString());
            }
        }

        // Get values object
        JsonObject valuesObj = new JsonObject();
        if (json.has("values")) {
            valuesObj = json.getAsJsonObject("values");
        } else {
            // Fallback: treat the entire object as values
            valuesObj = json;
        }

        Map<Identifier, Double> modifierMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : valuesObj.entrySet()) {
            try {
                Identifier aspectId = new Identifier(entry.getKey());
                double value = entry.getValue().getAsDouble();
                modifierMap.put(aspectId, value);
            } catch (Exception e) {
                AspectsLib.LOGGER.warn("Invalid aspect value in modifier: {} = {}", entry.getKey(), entry.getValue());
            }
        }

        return new AetherModifier(operation, modifierMap);
    }
}
