package dev.overgrown.aspectslib.mixin;

import dev.overgrown.aspectslib.aether.AetherDataHolder;
import dev.overgrown.aspectslib.aether.ChunkAetherData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements AetherDataHolder {
    @Unique
    private ChunkAetherData aetherData;

    @Override
    public ChunkAetherData getAetherData() {
        return aetherData;
    }

    @Override
    public void setAetherData(ChunkAetherData data) {
        this.aetherData = data;
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeAetherData(NbtCompound nbt, CallbackInfo ci) {
        if (aetherData != null) {
            nbt.put("AspectsLibAetherData", aetherData.toNbt());
        }
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readAetherData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("AspectsLibAetherData", NbtElement.COMPOUND_TYPE)) {
            aetherData = ChunkAetherData.fromNbt(nbt.getCompound("AspectsLibAetherData"));
        } else {
            aetherData = new ChunkAetherData();
        }
    }
}