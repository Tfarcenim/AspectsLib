package dev.overgrown.aspectslib.client.tooltip;

import dev.overgrown.aspectslib.data.AspectData;
import net.minecraft.client.item.TooltipData;

/**
 * Holds aspect data for tooltip rendering.
 * <p>
 * Passed to {@link AspectTooltipComponent} for rendering.
 * </p>
 */
public record AspectTooltipData(AspectData aspectData) implements TooltipData {
}