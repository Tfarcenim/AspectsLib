package dev.overgrown.aspectslib.mixin;

import dev.overgrown.aspectslib.aether.AetherManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkStatus.class)
public class ChunkEventsMixin {
    @Inject(method = "method_16567", at = @At("RETURN"))
    private static void afterChunkGeneration(ServerWorld world, Chunk chunk, CallbackInfo ci) {
        if (chunk.getStatus() == ChunkStatus.FULL) {
            AetherManager.initializeChunkAetherData(world, chunk); // Pass ServerWorld instance
        }
    }
}