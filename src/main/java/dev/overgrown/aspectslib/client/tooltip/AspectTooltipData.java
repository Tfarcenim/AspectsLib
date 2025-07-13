package dev.overgrown.aspectslib.client.tooltip;

import dev.overgrown.aspectslib.data.AspectData;
import net.minecraft.client.item.TooltipData;

public record AspectTooltipData(AspectData aspectData) implements TooltipData {
}