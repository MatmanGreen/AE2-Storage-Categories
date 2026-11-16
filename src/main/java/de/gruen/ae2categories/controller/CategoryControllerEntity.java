package de.gruen.ae2categories.controller;

import java.util.*;
import appeng.api.networking.*;
import appeng.items.storage.ViewCellItem;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class CategoryControllerEntity extends BlockEntity implements IInWorldGridNodeHost, MenuProvider {
    private final IManagedGridNode node = GridHelper.createManagedNode(this, new IGridNodeListener<CategoryControllerEntity>() {
        @Override public void onSaveChanges(CategoryControllerEntity owner, IGridNode node) { owner.setChanged(); }
    }).setInWorldNode(true).setExposedOnSides(EnumSet.allOf(Direction.class))
            .setIdlePowerUsage(0.5).setVisualRepresentation(ControllerRegistry.ITEM.get());
    public final ItemStackHandler cells = new ItemStackHandler(9) {
        @Override public boolean isItemValid(int slot, ItemStack stack) { return stack.getItem() instanceof ViewCellItem; }
        @Override public int getSlotLimit(int slot) { return 1; }
        @Override protected void onContentsChanged(int slot) { changed(); }
    };
    private UUID id = UUID.randomUUID();
    private String categoryName = "Kategorie";
    private int priority;
    private long revision;

    public CategoryControllerEntity(BlockPos pos, BlockState state) { super(ControllerRegistry.ENTITY.get(), pos, state); }
    public IManagedGridNode node() { return node; }
    public String categoryId() { return id.toString(); }
    public String categoryName() { return categoryName; }
    public int priority() { return priority; }
    public long revision() { return revision; }
    private void changed() { revision++; setChanged(); }
    public void configure(String name, int priority) {
        this.categoryName = cleanName(name);
        this.priority = Math.clamp(priority, -1000000, 1000000);
        changed();
    }
    public static String cleanName(String name) {
        String clean = name.replaceAll("[\\p{Cntrl}§]", "").strip();
        if (clean.isEmpty()) return "Kategorie";
        return clean.substring(0, Math.min(clean.length(), 64));
    }
    public List<ItemStack> copyCells() {
        var result = new ArrayList<ItemStack>();
        for (int i = 0; i < 9; i++) result.add(cells.getStackInSlot(i).copy());
        return result;
    }
    @Override public IGridNode getGridNode(Direction direction) { return node.getNode(); }
    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) GridHelper.onFirstTick(this, entity -> {
            if (!entity.isRemoved() && entity.level != null) entity.node.create(entity.level, entity.worldPosition);
        });
    }
    @Override public void setRemoved() { node.destroy(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { node.destroy(); super.onChunkUnloaded(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Cells", cells.serializeNBT(registries));
        tag.putString("CategoryName", categoryName);
        tag.putInt("Priority", priority);
        tag.putUUID("CategoryId", id);
        node.saveToNBT(tag);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        cells.deserializeNBT(registries, tag.getCompound("Cells"));
        categoryName = cleanName(tag.getString("CategoryName"));
        priority = Math.clamp(tag.getInt("Priority"), -1000000, 1000000);
        if (tag.hasUUID("CategoryId")) id = tag.getUUID("CategoryId");
        node.loadFromNBT(tag);
    }
    @Override public Component getDisplayName() { return Component.translatable("block.ae2categories.category_controller"); }
    @Override public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new CategoryControllerMenu(syncId, inventory, this);
    }
}
