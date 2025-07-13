package dev.overgrown.aspectslib.api;


import dev.overgrown.aspectslib.data.AspectData;

/**
 * Interface for objects that can provide aspect data.
 * Implemented via mixin on ItemStack.
 */
public interface IAspectDataProvider {
    /**
     * Gets the aspect data for this provider
     * @return The aspect data, or AspectData.DEFAULT if none
     */
    AspectData aspectslib$getAspectData();

    /**
     * Sets the aspect data for this provider
     * @param data The aspect data to set, or null to clear
     */
    void aspectslib$setAspectData(AspectData data);
}