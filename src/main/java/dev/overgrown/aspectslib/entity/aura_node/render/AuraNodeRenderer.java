package dev.overgrown.aspectslib.entity.aura_node.render;

import dev.overgrown.aspectslib.AspectsLib;
import dev.overgrown.aspectslib.entity.aura_node.AuraNodeEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AuraNodeRenderer extends EntityRenderer<AuraNodeEntity> {
    public AuraNodeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(AuraNodeEntity entity) {
        // Return different texture based on node type
        return AspectsLib.identifier("textures/entity/aura_node/" +
                entity.getNodeType().name().toLowerCase() + ".png");
    }

    @Override
    public void render(AuraNodeEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        // Base rendering logic (would be more complex in a real implementation)
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        // In a complete implementation, you would:
        // 1. Render the node base
        // 2. Render aspect icons around the node
        // 3. Add particle effects based on node type
    }
}