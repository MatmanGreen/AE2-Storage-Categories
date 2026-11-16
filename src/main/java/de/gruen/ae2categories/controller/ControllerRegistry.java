package de.gruen.ae2categories.controller;

import appeng.api.AECapabilities;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class ControllerRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "ae2categories");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "ae2categories");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "ae2categories");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "ae2categories");
    public static final DeferredHolder<Block, CategoryControllerBlock> BLOCK = BLOCKS.register("category_controller",
            () -> new CategoryControllerBlock(BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.METAL)));
    public static final DeferredHolder<Item, BlockItem> ITEM = ITEMS.register("category_controller", () -> new BlockItem(BLOCK.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CategoryControllerEntity>> ENTITY = ENTITIES.register("category_controller",
            () -> BlockEntityType.Builder.of(CategoryControllerEntity::new, BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<CategoryControllerMenu>> MENU = MENUS.register("category_controller",
            () -> IMenuTypeExtension.create(CategoryControllerMenu::new));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); MENUS.register(bus);
        bus.addListener(ControllerRegistry::capabilities);
        bus.addListener(ControllerRegistry::creative);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(AECapabilities.IN_WORLD_GRID_NODE_HOST, ENTITY.get(), (entity, context) -> entity);
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) event.accept(ITEM.get());
    }
}
