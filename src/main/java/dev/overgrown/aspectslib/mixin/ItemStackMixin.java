package dev.overgrown.aspectslib.mixin;

import dev.overgrown.aspectslib.api.IAspectDataProvider;
import dev.overgrown.aspectslib.data.AspectData;
import dev.overgrown.aspectslib.data.ItemAspectRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IAspectDataProvider {

    @Shadow public abstract Item getItem();
    @Shadow public abstract NbtCompound getOrCreateNbt();
    @Shadow public abstract NbtCompound getNbt();

    @Unique
    private AspectData aspectslib$cachedAspectData = null;

    @Unique
    private boolean aspectslib$aspectDataInitialized = false;

    @Override
    public AspectData aspectslib$getAspectData() {
        if (!aspectslib$aspectDataInitialized) {
            aspectslib$initializeAspectData();
            aspectslib$aspectDataInitialized = true;
        }

        if (aspectslib$cachedAspectData == null) {
            return AspectData.DEFAULT;
        }

        return aspectslib$cachedAspectData;
    }

    @Override
    public void aspectslib$setAspectData(AspectData data) {
        aspectslib$cachedAspectData = data;
        aspectslib$aspectDataInitialized = true;

        if (data != null && !data.isEmpty()) {
            NbtCompound nbt = getOrCreateNbt();
            nbt.put("AspectsLibData", data.toNbt());
        } else {
            NbtCompound nbt = getNbt();
            if (nbt != null) {
                nbt.remove("AspectsLibData");
            }
        }
    }

    @Unique
    private void aspectslib$initializeAspectData() {
        NbtCompound nbt = getNbt();
        if (nbt != null && nbt.contains("AspectsLibData")) {
            aspectslib$cachedAspectData = AspectData.fromNbt(nbt.getCompound("AspectsLibData"));
            return;
        }

        AspectData aspectData = new AspectData(new Object2IntOpenHashMap<>());

        Identifier itemId = Registries.ITEM.getId(getItem());

        if (ItemAspectRegistry.contains(itemId)) {
            aspectData = aspectData.addAspect(ItemAspectRegistry.get(itemId));
        }

        for (Map.Entry<Identifier, AspectData> entry : ItemAspectRegistry.entries()) {
            Identifier id = entry.getKey();
            AspectData itemAspectData = entry.getValue();

            if (itemId.equals(id)) {
                aspectData = aspectData.addAspect(itemAspectData);
            }
            
            TagKey<Item> tagKey = TagKey.of(Registries.ITEM.getKey(), id);
            if (getItem().getRegistryEntry().isIn(tagKey)) {
                aspectData = aspectData.addAspect(itemAspectData);
            }
        }

        aspectslib$cachedAspectData = aspectData.isEmpty() ? null : aspectData;
    }

    @Inject(method = "setNbt", at = @At("RETURN"))
    private void onSetNbt(NbtCompound nbt, CallbackInfo ci) {
        aspectslib$aspectDataInitialized = false;
        aspectslib$cachedAspectData = null;
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void onCopy(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack copy = cir.getReturnValue();
        if (aspectslib$cachedAspectData != null && !aspectslib$cachedAspectData.isEmpty()) {
            IAspectDataProvider copyProvider = (IAspectDataProvider) (Object) copy;
            copyProvider.aspectslib$setAspectData(aspectslib$cachedAspectData);
        }
    }
}