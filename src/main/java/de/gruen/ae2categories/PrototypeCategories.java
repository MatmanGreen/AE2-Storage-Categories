package de.gruen.ae2categories;

import java.util.List;
import appeng.api.stacks.AEItemKey;
import appeng.menu.me.common.GridInventoryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;

public final class PrototypeCategories {
    public static final List<String> ORDER = List.of("Holz", "Glas", "Create", "Baublöcke", "Sonstiges");
    public static String category(GridInventoryEntry entry) {
        if (!(entry.getWhat() instanceof AEItemKey key)) return "Sonstiges";
        var id = BuiltInRegistries.ITEM.getKey(key.getItem());
        if (id.getNamespace().equals("create")) return "Create";
        if (!(key.getItem() instanceof BlockItem)) return "Sonstiges";
        String path = id.getPath();
        if (path.contains("glass")) return "Glas";
        if (path.contains("planks") || path.contains("log") || path.contains("wood")
                || path.contains("oak") || path.contains("spruce") || path.contains("birch")
                || path.contains("jungle") || path.contains("acacia") || path.contains("mangrove")
                || path.contains("cherry") || path.contains("bamboo")) return "Holz";
        return "Baublöcke";
    }
}
