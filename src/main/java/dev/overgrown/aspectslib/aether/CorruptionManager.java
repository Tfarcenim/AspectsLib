package dev.overgrown.aspectslib.aether;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CorruptionManager {
    private static final Map<Identifier, List<CorruptionSource>> CORRUPTION_SOURCES = new ConcurrentHashMap<>();
    private static final Identifier VITIUM_ASPECT = new Identifier("aspectslib", "vitium");

    public static void addCorruptionSource(Identifier biomeId, BlockPos pos, int strength) {
        CORRUPTION_SOURCES.computeIfAbsent(biomeId, k -> new ArrayList<>())
                .add(new CorruptionSource(pos, strength));
    }

    public static void tick(ServerWorld world) {
        for (Map.Entry<Identifier, List<CorruptionSource>> entry : CORRUPTION_SOURCES.entrySet()) {
            Identifier biomeId = entry.getKey();
            List<CorruptionSource> sources = entry.getValue();

            // Get current modifications
            Map<Identifier, Double> modifications = DynamicAetherDensityManager.getModifications(biomeId);
            if (modifications == null) {
                continue;
            }

            // Check if vitium is dominant
            Double vitiumAmount = modifications.get(VITIUM_ASPECT);
            if (vitiumAmount == null || vitiumAmount <= 0) {
                continue;
            }

            double totalOtherAspects = modifications.entrySet().stream()
                    .filter(e -> !e.getKey().equals(VITIUM_ASPECT))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            // If vitium is dominant, convert other aspects to vitium
            if (vitiumAmount > totalOtherAspects) {
                for (Map.Entry<Identifier, Double> aspectEntry : modifications.entrySet()) {
                    if (aspectEntry.getKey().equals(VITIUM_ASPECT)) {
                        continue;
                    }
                    double convertedAmount = aspectEntry.getValue() * 0.1; // Convert 10% per check
                    DynamicAetherDensityManager.addModification(
                            biomeId,
                            aspectEntry.getKey(),
                            -convertedAmount
                    );
                    DynamicAetherDensityManager.addModification(
                            biomeId,
                            VITIUM_ASPECT,
                            convertedAmount
                    );
                }

                // If only vitium remains, replace random blocks with sculk from each source
                if (totalOtherAspects <= 0) {
                    for (CorruptionSource source : sources) {
                        if (world.getRandom().nextFloat() < 0.1f) { // 10% chance per source per tick
                            replaceRandomBlockWithSculk(world, source.position);
                        }
                    }
                }
            }
        }
    }

    private static void replaceRandomBlockWithSculk(World world, BlockPos centerPos) {
        // Get a random position within 16 blocks
        BlockPos targetPos = centerPos.add(
                world.random.nextInt(32) - 16,
                world.random.nextInt(8) - 4,
                world.random.nextInt(32) - 16
        );

        // Check if the block is replaceable
        if (!world.getBlockState(targetPos).isAir() &&
                !(world.getBlockState(targetPos).getBlock() instanceof SculkSpreadable)) {
            // Replace with sculk block
            world.setBlockState(targetPos, Blocks.SCULK.getDefaultState());

            // Visual and sound effects
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SOUL,
                        targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
                        5, 0.2, 0.2, 0.2, 0.0);
                world.playSound(null, targetPos, SoundEvents.BLOCK_SCULK_PLACE,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static class CorruptionSource {
        public final BlockPos position;
        public final int strength;

        public CorruptionSource(BlockPos position, int strength) {
            this.position = position;
            this.strength = strength;
        }
    }
}