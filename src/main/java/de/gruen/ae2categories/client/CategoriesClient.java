package de.gruen.ae2categories.client;

import de.gruen.ae2categories.controller.ControllerRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "ae2categories", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CategoriesClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event) {
        event.register(ControllerRegistry.MENU.get(), CategoryControllerScreen::new);
    }
}
