package dev.overgrown.aspectslib.registry;

import dev.overgrown.aspectslib.AspectsLib;
import dev.overgrown.aspectslib.entity.aura_node.AuraNodeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {
    public static final EntityType<AuraNodeEntity> AURA_NODE = EntityType.Builder.create(AuraNodeEntity::new, SpawnGroup.AMBIENT)
            .setDimensions(0.5f, 0.5f)
            .maxTrackingRange(64)
            .trackingTickInterval(3)
            .build("");

    public static void register() {
        Registry.register(Registries.ENTITY_TYPE, AspectsLib.identifier("aura_node"), AURA_NODE);
    }
}