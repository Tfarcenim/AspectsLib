package dev.overgrown.aspectslib.registry;

import dev.overgrown.aspectslib.AspectsLib;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {

    //======================================================================
    // Aspect Shards
    //======================================================================

    public static final Item AER_ASPECT_SHARD = registerItem("aer_aspect_shard",
            new Item(new FabricItemSettings()
                    .maxCount(64)
            )
    );

    public static final Item ALIENIS_ASPECT_SHARD = registerItem("alienis_aspect_shard",
            new Item(new FabricItemSettings()
                    .maxCount(64)
            )
    );

    public static final Item FAMES_ASPECT_SHARD = registerItem("fames_aspect_shard",
            new Item(new FabricItemSettings()
                    .maxCount(64)
            )
    );

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, AspectsLib.identifier(name), item);
    }

    public static void initialize() {
        // Initialization handled by static field loading
    }
}