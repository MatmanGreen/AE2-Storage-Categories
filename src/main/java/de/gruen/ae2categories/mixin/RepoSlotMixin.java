package de.gruen.ae2categories.mixin;

import appeng.client.gui.me.common.Repo;
import appeng.client.gui.me.common.RepoSlot;
import de.gruen.ae2categories.CategoryView;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.*;

@Mixin(value = RepoSlot.class, remap = false)
public abstract class RepoSlotMixin extends Slot {
    @Shadow @Final private Repo repo;
    @Shadow @Final private int offset;
    protected RepoSlotMixin() { super(null, 0, 0, 0); }
    @Override public boolean isActive() {
        return ((CategoryView) repo).categories$header(offset) == null && super.isActive();
    }
}
