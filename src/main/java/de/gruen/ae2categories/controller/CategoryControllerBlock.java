package de.gruen.ae2categories.controller;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CategoryControllerBlock extends BaseEntityBlock {
    public static final MapCodec<CategoryControllerBlock> CODEC = simpleCodec(CategoryControllerBlock::new);
    public CategoryControllerBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new CategoryControllerEntity(pos, state); }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof CategoryControllerEntity entity) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(entity, buf -> {
                    buf.writeBlockPos(pos);
                    buf.writeUtf(entity.categoryName(), 64);
                    buf.writeInt(entity.priority());
                });
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player && level.getBlockEntity(pos) instanceof CategoryControllerEntity entity)
            entity.node().setOwningPlayer(player);
    }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CategoryControllerEntity entity && !level.isClientSide) {
            for (int slot = 0; slot < entity.cells.getSlots(); slot++) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity.cells.getStackInSlot(slot));
                entity.cells.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}
