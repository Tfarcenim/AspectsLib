package dev.overgrown.aspectslib.aether;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ChunkAetherData {
    private final Object2DoubleMap<Identifier> densities;
    private boolean permanentDeadZone;

    public ChunkAetherData() {
        this.densities = new Object2DoubleOpenHashMap<>();
        this.permanentDeadZone = false;
    }

    public void setDensity(Identifier aspectId, double density) {
        densities.put(aspectId, density);
    }

    public double getDensity(Identifier aspectId) {
        if (permanentDeadZone) return 0.0;
        return densities.getDouble(aspectId);
    }

    public Map<Identifier, Double> getDensities() {
        return densities;
    }

    public boolean isPermanentDeadZone() {
        return permanentDeadZone;
    }

    public void setPermanentDeadZone(boolean permanentDeadZone) {
        this.permanentDeadZone = permanentDeadZone;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList densityList = new NbtList();

        for (Object2DoubleMap.Entry<Identifier> entry : densities.object2DoubleEntrySet()) {
            NbtCompound aspectEntry = new NbtCompound();
            aspectEntry.putString("Aspect", entry.getKey().toString());
            aspectEntry.putDouble("Density", entry.getDoubleValue());
            densityList.add(aspectEntry);
        }

        nbt.put("AetherDensities", densityList);
        nbt.putBoolean("PermanentDeadZone", permanentDeadZone);
        return nbt;
    }

    public static ChunkAetherData fromNbt(NbtCompound nbt) {
        ChunkAetherData data = new ChunkAetherData();
        if (nbt.contains("AetherDensities", NbtElement.LIST_TYPE)) {
            NbtList densityList = nbt.getList("AetherDensities", NbtElement.COMPOUND_TYPE);
            for (NbtElement element : densityList) {
                NbtCompound aspectEntry = (NbtCompound) element;
                Identifier aspectId = Identifier.tryParse(aspectEntry.getString("Aspect"));
                double density = aspectEntry.getDouble("Density");
                data.setDensity(aspectId, density);
            }
        }
        data.setPermanentDeadZone(nbt.getBoolean("PermanentDeadZone"));
        return data;
    }
}