package de.gruen.ae2categories.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ConfigurePayload(int menuId, String name, int priority) implements CustomPacketPayload {
    public static final Type<ConfigurePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("ae2categories", "configure"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigurePayload> CODEC = new StreamCodec<>() {
        @Override public ConfigurePayload decode(RegistryFriendlyByteBuf buf) { return new ConfigurePayload(buf.readVarInt(), buf.readUtf(64), buf.readInt()); }
        @Override public void encode(RegistryFriendlyByteBuf buf, ConfigurePayload value) { buf.writeVarInt(value.menuId); buf.writeUtf(value.name, 64); buf.writeInt(value.priority); }
    };
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
