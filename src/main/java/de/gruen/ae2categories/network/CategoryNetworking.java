package de.gruen.ae2categories.network;

import java.util.*;
import appeng.menu.me.common.MEStorageMenu;
import de.gruen.ae2categories.client.ClientCategoryState;
import de.gruen.ae2categories.controller.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class CategoryNetworking {
    private static final Map<MEStorageMenu, String> SENT = new WeakHashMap<>();
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(CategoriesPayload.TYPE, CategoriesPayload.CODEC, (payload, context) ->
                context.enqueueWork(() -> ClientCategoryState.receive(payload)));
        registrar.playToServer(ConfigurePayload.TYPE, ConfigurePayload.CODEC, (payload, context) -> context.enqueueWork(() -> {
            var player = context.player();
            if (!player.isSpectator() && player.containerMenu instanceof CategoryControllerMenu menu
                    && menu.containerId == payload.menuId() && menu.controller != null && menu.stillValid(player)) {
                menu.controller.configure(payload.name(), payload.priority());
                player.closeContainer();
            }
        }));
    }
    public static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 10 != 0
                || !(player.containerMenu instanceof MEStorageMenu menu)) return;
        var node = menu.getGridNode();
        List<CategoryControllerEntity> controllers = node == null || !node.isActive() ? List.of()
                : node.getGrid().getActiveMachines(CategoryControllerEntity.class).stream()
                .sorted(Comparator.comparingInt(CategoryControllerEntity::priority).reversed()
                        .thenComparing(CategoryControllerEntity::categoryId)).toList();
        // The prototype is intended for a small number of controllers. Explicitly report the bound.
        boolean limited = controllers.size() > 256;
        if (limited) controllers = controllers.subList(0, 256);
        String signature = controllers.stream().map(c -> c.categoryId() + ":" + c.revision()).reduce(limited ? "limited" : "", (a, b) -> a + ";" + b);
        if (signature.equals(SENT.get(menu))) return;
        SENT.put(menu, signature);
        if (limited) player.displayClientMessage(Component.literal("AE2 Categories: maximal 256 Controller pro Netzwerk in diesem Prototyp."), false);
        var definitions = controllers.stream().map(c -> new CategoryDefinition(c.categoryId(), c.categoryName(), c.copyCells())).toList();
        PacketDistributor.sendToPlayer(player, new CategoriesPayload(menu.containerId, definitions));
    }
}
