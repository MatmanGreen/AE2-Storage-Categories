package de.gruen.ae2categories.controller;

import appeng.items.storage.ViewCellItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.*;

public final class CategoryControllerMenu extends AbstractContainerMenu {
    public final CategoryControllerEntity controller;
    public final BlockPos pos;
    public final String initialName;
    public final int initialPriority;
    private final ContainerData status;

    public CategoryControllerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf data) {
        this(id, inventory, null, data.readBlockPos(), data.readUtf(64), data.readInt());
    }
    public CategoryControllerMenu(int id, Inventory inventory, CategoryControllerEntity controller) {
        this(id, inventory, controller, controller.getBlockPos(), controller.categoryName(), controller.priority());
    }
    private CategoryControllerMenu(int id, Inventory inventory, CategoryControllerEntity controller, BlockPos pos, String name, int priority) {
        super(ControllerRegistry.MENU.get(), id);
        this.controller = controller; this.pos = pos; initialName = name; initialPriority = priority;
        IItemHandler handler = controller != null ? controller.cells : new ItemStackHandler(9);
        for (int i = 0; i < 9; i++) addSlot(new SlotItemHandler(handler, i, 8 + i * 18, 84) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof ViewCellItem; }
            @Override public int getMaxStackSize() { return 1; }
        });
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 126 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 184));
        status = controller == null ? new SimpleContainerData(1) : new ContainerData() {
            public int get(int index) { return controller.node().isActive() ? 1 : 0; }
            public void set(int index, int value) {}
            public int getCount() { return 1; }
        };
        addDataSlots(status);
    }
    public boolean connected() { return status.get(0) == 1; }
    @Override public boolean stillValid(Player player) {
        return controller == null || (!controller.isRemoved() && player.level() == controller.getLevel()
                && player.level().getBlockEntity(pos) == controller && player.distanceToSqr(pos.getCenter()) <= 64);
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem(), original = stack.copy();
        if (index < 9) {
            if (!moveItemStackTo(stack, 9, slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof ViewCellItem) {
            if (!moveItemStackTo(stack, 0, 9, false)) return ItemStack.EMPTY;
        } else {
            if (index < 36 ? !moveItemStackTo(stack, 36, 45, false) : !moveItemStackTo(stack, 9, 36, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
