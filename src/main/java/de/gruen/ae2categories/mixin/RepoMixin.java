package de.gruen.ae2categories.mixin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import appeng.client.gui.me.common.Repo;
import appeng.client.gui.widgets.IScrollSource;
import appeng.menu.me.common.GridInventoryEntry;
import de.gruen.ae2categories.*;
import de.gruen.ae2categories.client.ClientCategoryState;
import de.gruen.ae2categories.client.CollapsePreferences;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = Repo.class, remap = false)
public abstract class RepoMixin implements CategoryView {
    @Shadow @Final private ArrayList<GridInventoryEntry> view;
    @Shadow @Final private ArrayList<GridInventoryEntry> pinnedRow;
    @Shadow @Final private IScrollSource src;
    @Shadow private int rowSize;
    @Shadow public abstract String getSearchString();
    @Shadow public abstract boolean isPaused();
    @Unique private List<RowLayout.Cell<GridInventoryEntry>> categories$layout = List.of();
    @Unique private boolean categories$active;
    @Unique private int categories$columns;
    @Unique private final Set<String> categories$collapsed = new HashSet<>();
    @Unique private boolean categories$forceRebuild;

    @Inject(method = "updateView", at = @At(value = "FIELD", target = "Lappeng/client/gui/me/common/Repo;updateViewListener:Ljava/lang/Runnable;", ordinal = 0))
    private void categories$rebuild(CallbackInfo ci) {
        categories$collapsed.clear();
        categories$collapsed.addAll(CollapsePreferences.collapsed());
        boolean active = CategoriesMod.enabled && getSearchString().isEmpty();
        if (!active) {
            categories$active = false;
            categories$layout = List.of();
            return;
        }
        if (isPaused() && categories$active && categories$columns == rowSize && !categories$forceRebuild) {
            // Keep every position fixed while Shift is held; only refresh quantities.
            var bySerial = new HashMap<Long, GridInventoryEntry>();
            for (var entry : view) bySerial.put(entry.getSerial(), entry);
            var refreshed = new ArrayList<RowLayout.Cell<GridInventoryEntry>>(categories$layout.size());
            for (var cell : categories$layout) {
                var item = cell.item();
                refreshed.add(new RowLayout.Cell<>(cell.header(), item == null ? null : bySerial.getOrDefault(item.getSerial(), item)));
            }
            categories$layout = refreshed;
        } else {
            Repo self = (Repo) (Object) this;
            categories$layout = RowLayout.build(view, rowSize, ClientCategoryState.order(self),
                    entry -> ClientCategoryState.category(self, entry), categories$collapsed);
        }
        categories$columns = rowSize;
        categories$active = true;
        categories$forceRebuild = false;
    }

    @Unique private int categories$index(int visibleSlot) {
        if (!pinnedRow.isEmpty()) {
            if (visibleSlot < rowSize) return -1;
            visibleSlot -= rowSize;
        }
        return visibleSlot + src.getCurrentScroll() * rowSize;
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void categories$get(int idx, CallbackInfoReturnable<GridInventoryEntry> cir) {
        if (!categories$active) return;
        int index = categories$index(idx);
        if (index < 0) return; // Let AE2 handle its pinned row.
        cir.setReturnValue(index < categories$layout.size() ? categories$layout.get(index).item() : null);
    }

    @Inject(method = "size", at = @At("HEAD"), cancellable = true)
    private void categories$size(CallbackInfoReturnable<Integer> cir) {
        // MEStorageScreen adds the fixed pinned row separately to its scrollbar.
        if (categories$active) cir.setReturnValue(categories$layout.size());
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void categories$clear(CallbackInfo ci) {
        categories$active = false;
        categories$layout = List.of();
    }

    @Override public String categories$header(int visibleSlot) {
        if (!categories$active) return null;
        int index = categories$index(visibleSlot);
        return index >= 0 && index < categories$layout.size() ? categories$layout.get(index).header() : null;
    }

    @Override public int categories$itemCount() { return view.size() + pinnedRow.size(); }

    @Override public boolean categories$isCollapsed(String title) { return categories$collapsed.contains(title); }
    @Override public void categories$invalidate() { categories$forceRebuild = true; }

    @Override public void categories$toggle(String title) {
        if (!categories$active) return;
        if (!categories$collapsed.add(title)) categories$collapsed.remove(title);
        CollapsePreferences.setCollapsed(title, categories$collapsed.contains(title));
        categories$forceRebuild = true;
        ((Repo) (Object) this).updateView();
    }
}
