package dev.overgrown.aspectslib.client.tooltip;

import dev.overgrown.aspectslib.data.Aspect;
import dev.overgrown.aspectslib.data.AspectData;
import dev.overgrown.aspectslib.data.ModRegistries;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class AspectTooltipComponent implements TooltipComponent {
    private final AspectData aspectData;

    public AspectTooltipComponent(AspectTooltipData data) {
        this.aspectData = data.aspectData();
    }

    private boolean shouldShowNames() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.currentScreen != null && Screen.hasShiftDown();
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        boolean showNames = shouldShowNames();
        int width = 0;
        for (Object2IntMap.Entry<Identifier> entry : aspectData.getMap().object2IntEntrySet()) {
            Identifier aspectId = entry.getKey();
            Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
            if (aspect == null) continue;

            int valueWidth = showNames ?
                    textRenderer.getWidth(aspect.getTranslatedName()) :
                    textRenderer.getWidth(String.valueOf(entry.getIntValue()));
            width += 16 + 2 + valueWidth + 4;
        }
        return width;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        boolean showNames = shouldShowNames();
        int currentX = x;
        final int TEXT_COLOR = 0xFFFFFFFF;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for (Object2IntMap.Entry<Identifier> entry : aspectData.getMap().object2IntEntrySet()) {
            int value = entry.getIntValue();
            Identifier aspectId = entry.getKey();

            Aspect aspect = ModRegistries.ASPECTS.get(aspectId);
            if (aspect == null) continue;

            Identifier texture = aspect.textureLocation();

            RenderSystem.setShaderTexture(0, texture);
            context.drawTexture(texture, currentX, y, 0, 0, 16, 16, 16, 16);

            int textY = y + 5;

            if (showNames) {
                Text aspectName = aspect.getTranslatedName().formatted(Formatting.WHITE);
                context.drawText(textRenderer, aspectName, currentX + 18, textY, TEXT_COLOR, false);
                currentX += 16 + textRenderer.getWidth(aspectName) + 6;
            } else {
                String valueStr = String.valueOf(value);
                context.drawText(textRenderer, valueStr, currentX + 18, textY, TEXT_COLOR, false);
                currentX += 16 + textRenderer.getWidth(valueStr) + 6;
            }
        }
    }
}