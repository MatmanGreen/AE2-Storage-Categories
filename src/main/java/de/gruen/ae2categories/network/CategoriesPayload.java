package de.gruen.ae2categories.network;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record CategoriesPayload(int menuId, List<CategoryDefinition> categories) implements CustomPacketPayload {
    public static final Type<CategoriesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("ae2categories", "categories"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CategoriesPayload> CODEC = new StreamCodec<>() {
        @Override public CategoriesPayload decode(RegistryFriendlyByteBuf buf) {
            int menu = buf.readVarInt();
            int count = buf.readVarInt();
            if (count < 0 || count > 256) throw new IllegalArgumentException("Invalid category count");
            var categories = new ArrayList<CategoryDefinition>();
            for (int c = 0; c < count; c++) {
                String id = buf.readUtf(36), name = buf.readUtf(64);
                var cells = new ArrayList<ItemStack>();
                for (int slot = 0; slot < 9; slot++) cells.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
                categories.add(new CategoryDefinition(id, name, List.copyOf(cells)));
            }
            return new CategoriesPayload(menu, List.copyOf(categories));
        }
        @Override public void encode(RegistryFriendlyByteBuf buf, CategoriesPayload value) {
            buf.writeVarInt(value.menuId); buf.writeVarInt(value.categories.size());
            for (var category : value.categories) {
                buf.writeUtf(category.id(), 36); buf.writeUtf(category.name(), 64);
                for (var stack : category.cells()) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stack);
            }
        }
    };
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
