package de.gruen.ae2categories.client;

import java.util.*;
import appeng.client.gui.me.common.Repo;
import appeng.items.storage.ViewCellItem;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.util.prioritylist.IPartitionList;
import de.gruen.ae2categories.CategoryView;
import de.gruen.ae2categories.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class ClientCategoryState {
    public static final String OTHER = "__other__";
    private record Filter(String id, String name, IPartitionList predicate) {}
    private record State(List<String> order, Map<String, String> labels, List<Filter> filters) {}
    private static final State EMPTY = new State(List.of(OTHER), Map.of(OTHER, "Sonstiges"), List.of());
    private static final Map<AbstractContainerMenu, State> RECEIVED = new WeakHashMap<>();
    private static final Map<Repo, State> REPOS = new WeakHashMap<>();

    public static void receive(CategoriesPayload payload) {
        var player = Minecraft.getInstance().player;
        if (player == null || player.containerMenu.containerId != payload.menuId()) return;
        var order = new ArrayList<String>();
        var labels = new HashMap<String, String>();
        var filters = new ArrayList<Filter>();
        for (var category : payload.categories()) {
            order.add(category.id()); labels.put(category.id(), category.name());
            filters.add(new Filter(category.id(), category.name(), ViewCellItem.createItemFilter(category.cells())));
        }
        order.add(OTHER); labels.put(OTHER, "Sonstiges");
        RECEIVED.put(player.containerMenu, new State(List.copyOf(order), Map.copyOf(labels), List.copyOf(filters)));
    }
    public static void bind(Repo repo, AbstractContainerMenu menu) {
        var state = RECEIVED.getOrDefault(menu, EMPTY);
        if (REPOS.get(repo) != state) {
            REPOS.put(repo, state);
            ((CategoryView) repo).categories$invalidate();
            repo.updateView();
        }
    }
    public static List<String> order(Repo repo) { return REPOS.getOrDefault(repo, EMPTY).order; }
    public static String label(Repo repo, String id) { return REPOS.getOrDefault(repo, EMPTY).labels.getOrDefault(id, id); }
    public static String category(Repo repo, GridInventoryEntry entry) {
        for (var filter : REPOS.getOrDefault(repo, EMPTY).filters) {
            if (filter.predicate != null && entry.getWhat() instanceof appeng.api.stacks.AEItemKey
                    && filter.predicate.isListed(entry.getWhat())) return filter.id;
        }
        return OTHER;
    }
}
