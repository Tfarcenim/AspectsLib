package dev.overgrown.aspectslib.api;

import dev.overgrown.aspectslib.data.AspectData;

public interface IAspectAffinityEntity {
    AspectData aspectslib$getOriginalAspectData();
    void aspectslib$setOriginalAspectData(AspectData data);

    AspectData aspectslib$getAspectData();
    void aspectslib$setAspectData(AspectData data);
}