package de.gruen.ae2categories.network;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** Ordered by the server; identity is independent of the display name. */
public record CategoryDefinition(String id, String name, List<ItemStack> cells) {}
