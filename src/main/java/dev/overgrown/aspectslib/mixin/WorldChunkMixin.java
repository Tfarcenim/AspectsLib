package dev.overgrown.aspectslib.mixin;

import dev.overgrown.aspectslib.aether.AetherDataHolder;
import dev.overgrown.aspectslib.aether.ChunkAetherData;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin implements AetherDataHolder {
    @Unique
    private ChunkAetherData aspectslib$aetherData;

    @Unique
    public ChunkAetherData aspectslib$getAetherData() {
        return aspectslib$aetherData;
    }

    @Unique
    public void aspectslib$setAetherData(ChunkAetherData data) {
        this.aspectslib$aetherData = data;
    }
}
