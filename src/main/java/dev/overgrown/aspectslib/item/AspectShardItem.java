package dev.overgrown.aspectslib.item;

import dev.overgrown.aspectslib.api.IAspectDataProvider;
import dev.overgrown.aspectslib.data.AspectData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class AspectShardItem extends Item implements IAspectDataProvider {
    private final String aspectName;

    public AspectShardItem(String aspectName, Settings settings) {
        super(settings);
        this.aspectName = aspectName;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        setAspectData(stack);
        return stack;
    }

    private void setAspectData(ItemStack stack) {
        AspectData.Builder builder = new AspectData.Builder(AspectData.DEFAULT);
        builder.addByName(aspectName, 1);
        stack.setSubNbt("AspectsLibData", builder.build().toNbt());
    }

    @Override
    public AspectData aspectslib$getAspectData() {
        return AspectData.DEFAULT; // Implement actual logic
    }

    @Override
    public void aspectslib$setAspectData(AspectData data) {
        // Implement actual logic
    }
}