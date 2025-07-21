package dev.overgrown.aspectslib.entity.aura_node.world;

import dev.overgrown.aspectslib.entity.aura_node.AuraNodeEntity;
import dev.overgrown.aspectslib.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.SpawnHelper;

public class AuraNodeSpawner {
    public static void spawnNodes(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (!spawnMonsters) return;

        Random random = world.getRandom();
        ChunkPos spawnChunk = new ChunkPos(world.getSpawnPos());

        for (int i = 0; i < 3; i++) { // Try 3 times per chunk
            // Calculate random position near spawn
            int chunkXOffset = random.nextInt(32) - 16;
            int chunkZOffset = random.nextInt(32) - 16;
            int startX = (spawnChunk.x + chunkXOffset) * 16;
            int startZ = (spawnChunk.z + chunkZOffset) * 16;

            BlockPos pos = new BlockPos(
                    random.nextInt(16) + startX,
                    world.getSeaLevel() + random.nextInt(64),
                    random.nextInt(16) + startZ
            );

            // Check if position is valid for spawning
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            if (!SpawnHelper.isClearForSpawn(world, pos, blockState, fluidState, ModEntities.AURA_NODE)) continue;

            // Determine node type
            AuraNodeEntity.NodeType nodeType;
            float roll = random.nextFloat();

            if (roll < 0.0157f) { // 1.57% chance
                nodeType = roll < 0.0056f ?
                        AuraNodeEntity.NodeType.HUNGRY : // 0.56% chance
                        (roll < 0.0157f ?
                                AuraNodeEntity.NodeType.PURE : // 1.01% chance
                                AuraNodeEntity.NodeType.SINISTER); // 1.01% chance
            } else {
                nodeType = random.nextFloat() < 0.8f ?
                        AuraNodeEntity.NodeType.NORMAL :
                        AuraNodeEntity.NodeType.UNSTABLE;
            }

            // Create and spawn node
            AuraNodeEntity node = new AuraNodeEntity(ModEntities.AURA_NODE, world);
            node.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            node.setNodeType(nodeType);
            node.initializeAspects(random);

            world.spawnEntity(node);
        }
    }
}