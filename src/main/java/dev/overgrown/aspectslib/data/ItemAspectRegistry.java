package dev.overgrown.aspectslib.data;

import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Stream;

/**
 * This class pairs AspectData to Identifiers, used in applying custom AspectData to items based on the items
 * Identifier.
 */
public class ItemAspectRegistry {

    /**
     * Internal storage for the mappings between Identifiers and AspectData
     */
    private static final HashMap<Identifier, AspectData> idToAspect = new HashMap<>();

    /**
     * Registers a new association between an `Identifier` and an `AspectData`.
     *
     * @param id     The `Identifier` of the items to which the aspect will be applied.
     * @param aspect The `AspectData` to associate with the items.
     * @return The registered `AspectData`.
     */
    public static AspectData register(Identifier id, AspectData aspect) {
        if(idToAspect.containsKey(id)) {
            AspectData existing = idToAspect.get(id);
            existing.addAspect(aspect);
            return aspect;
        }
        idToAspect.put(id, aspect);
        return aspect;
    }

    /**
     * Updates an existing association between an `Identifier` and an `AspectData`. If the `Identifier` is already
     * registered, the old `AspectData` is removed and replaced with the new one.
     *
     * @param id     The `Identifier` of the items to update.
     * @param aspect The new `AspectData` to associate with the items.
     */
    protected static void update(Identifier id, AspectData aspect) {
        if(idToAspect.containsKey(id)) {
            AspectData old = idToAspect.get(id);
            idToAspect.remove(id);
        }
        register(id, aspect);
    }

    /**
     * Removes the association for the specified `Identifier`.
     *
     * @param id The `Identifier` of the items to remove from the registry.
     */
    protected static void remove(Identifier id) {
        idToAspect.remove(id);
    }

    /**
     * Returns the number of registered items-aspect associations.
     *
     * @return The number of entries in the registry.
     */
    public static int size() {
        return idToAspect.size();
    }

    /**
     * Provides a stream of all registered `Identifier` objects.
     *
     * @return A stream of `Identifier` objects.
     */
    public static Stream<Identifier> identifiers() {
        return idToAspect.keySet().stream();
    }

    /**
     * Provides a set of all registered entries (key-value pairs) in the registry.
     *
     * @return A set of `Map.Entry` objects representing the registered associations.
     */
    public static Set<Map.Entry<Identifier, AspectData>> entries() {
        return idToAspect.entrySet();
    }

    /**
     * Provides a list of all registered `AspectData` objects.
     *
     * @return A list of `AspectData` objects.
     */
    public static List<AspectData> values() {
        return idToAspect.values().stream().toList();
    }

    /**
     * Retrieves the `AspectData` associated with the specified `Identifier`.
     *
     * @param id The `Identifier` of the items to look up.
     * @return The associated `AspectData`, or DEFAULT if not found.
     */
    public static AspectData get(Identifier id) {
        return idToAspect.getOrDefault(id, AspectData.DEFAULT);
    }

    /**
     * Retrieves the `Identifier` associated with the specified `AspectData`.
     *
     * @param aspect The `AspectData` to look up.
     * @return The associated `Identifier`.
     * @throws IllegalArgumentException If the `AspectData` is not registered.
     */
    public static Identifier getId(AspectData aspect) {
        Optional<Map.Entry<Identifier, AspectData>> entryOptional = idToAspect.entrySet().stream()
                .filter((aspectEntry) -> Objects.equals(aspectEntry.getValue(), aspect))
                .findFirst();
        if(entryOptional.isPresent()) {
            return entryOptional.get().getKey();
        } else {
            throw new IllegalArgumentException("Could not get identifier from aspect data '" + aspect.toString() + "', as it was not registered!");
        }
    }

    /**
     * Checks if the registry contains an association for the specified `Identifier`.
     *
     * @param id The `Identifier` to check.
     * @return `true` if the `Identifier` is registered, otherwise `false`.
     */
    public static boolean contains(Identifier id) {
        return idToAspect.containsKey(id);
    }

    /**
     * Clears all registered associations from the registry.
     */
    public static void clear() {
        idToAspect.clear();
    }

    /**
     * Resets the registry by clearing all registered associations. This is an alias for {@link #clear()}.
     */
    public static void reset() {
        clear();
    }
}