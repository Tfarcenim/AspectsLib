package dev.overgrown.aspectslib.mixin.client;

import dev.overgrown.aspectslib.api.IAspectDataProvider;
import dev.overgrown.aspectslib.client.tooltip.AspectTooltipData;
import dev.overgrown.aspectslib.data.AspectData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public abstract class ItemStackClientMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    private void addAspectTooltipData(CallbackInfoReturnable<Optional<TooltipData>> cir) {
        IAspectDataProvider provider = (IAspectDataProvider) this;
        AspectData aspectData = provider.aspectslib$getAspectData();

        if (aspectData == null || aspectData.isEmpty()) {
            return;
        }

        cir.setReturnValue(Optional.of(new AspectTooltipData(aspectData)));
    }
}