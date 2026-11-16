package de.gruen.ae2categories.dev;

import java.util.List;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.items.storage.ViewCellItem;
import de.gruen.ae2categories.controller.*;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.*;

@GameTestHolder("ae2categories")
@PrefixGameTestTemplate(false)
public final class ControllerGameTests {
    private static ItemStack cell(Item item) {
        var cell = AEItems.VIEW_CELL.stack();
        ((ICellWorkbenchItem) cell.getItem()).getConfigInventory(cell).addFilter(AEItemKey.of(item));
        return cell;
    }
    @GameTest(template = "test_empty", timeoutTicks = 40)
    public static void filtersAndPersistence(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ControllerRegistry.BLOCK.get());
        CategoryControllerEntity entity = helper.getBlockEntity(pos);
        entity.configure("Meine Blöcke", 42);
        entity.cells.setStackInSlot(0, cell(Items.OAK_PLANKS));
        entity.cells.setStackInSlot(1, cell(Items.GLASS));
        var filter = ViewCellItem.createItemFilter(entity.copyCells());
        helper.assertTrue(filter != null && filter.isListed(AEItemKey.of(Items.OAK_PLANKS)), "First cell must match");
        helper.assertTrue(filter.isListed(AEItemKey.of(Items.GLASS)), "Second cell must be merged");
        helper.assertTrue(!filter.isListed(AEItemKey.of(Items.DIAMOND)), "Unlisted item must not match");
        helper.assertTrue(!entity.cells.isItemValid(2, new ItemStack(Items.DIAMOND)), "Only View Cells accepted");
        var registries = helper.getLevel().registryAccess();
        var saved = entity.saveWithFullMetadata(registries);
        var restored = new CategoryControllerEntity(entity.getBlockPos(), entity.getBlockState());
        restored.loadWithComponents(saved, registries);
        helper.assertTrue(restored.categoryName().equals("Meine Blöcke") && restored.priority() == 42, "Name and priority survive reload");
        helper.assertTrue(restored.categoryId().equals(entity.categoryId()), "Stable identity survives reload");
        helper.assertTrue(ItemStack.matches(entity.cells.getStackInSlot(0), restored.cells.getStackInSlot(0)), "View Cell components survive reload");
        var restoredFilter = ViewCellItem.createItemFilter(restored.copyCells());
        helper.assertTrue(restoredFilter != null && restoredFilter.isListed(AEItemKey.of(Items.GLASS)), "Reloaded second cell filter works");
        entity.cells.setStackInSlot(0, ItemStack.EMPTY);
        entity.cells.setStackInSlot(1, ItemStack.EMPTY);
        helper.assertTrue(ViewCellItem.createItemFilter(entity.copyCells()) == null, "Empty controller must not match everything");
        helper.succeed();
    }

    @GameTest(template = "test_empty", timeoutTicks = 160)
    public static void networkIsolationAndRemoval(GameTestHelper helper) {
        BlockPos a = new BlockPos(1, 1, 1), b = new BlockPos(5, 1, 1);
        helper.setBlock(a, ControllerRegistry.BLOCK.get());
        helper.setBlock(a.east(), AEBlocks.CREATIVE_ENERGY_CELL.block());
        helper.setBlock(b, ControllerRegistry.BLOCK.get());
        helper.setBlock(b.east(), AEBlocks.CREATIVE_ENERGY_CELL.block());
        helper.runAfterDelay(80, () -> {
            CategoryControllerEntity first = helper.getBlockEntity(a), second = helper.getBlockEntity(b);
            helper.assertTrue(first.node().isActive() && second.node().isActive(), "Both controllers join powered ME grids");
            helper.assertTrue(first.node().getGrid() != second.node().getGrid(), "Separate grids must stay separate");
            helper.assertTrue(first.node().getGrid().getActiveMachines(CategoryControllerEntity.class).equals(java.util.Set.of(first)), "Grid must enumerate only its own controller");
            var oldGrid = first.node().getGrid();
            helper.setBlock(a, Blocks.AIR);
            helper.assertTrue(first.node().getNode() == null, "Breaking controller destroys node");
            helper.assertTrue(!oldGrid.getMachines(CategoryControllerEntity.class).contains(first), "Removed controller leaves grid");
            helper.succeed();
        });
    }
}
