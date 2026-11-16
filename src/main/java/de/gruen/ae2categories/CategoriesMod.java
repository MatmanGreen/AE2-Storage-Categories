package de.gruen.ae2categories;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import de.gruen.ae2categories.controller.ControllerRegistry;
import de.gruen.ae2categories.network.CategoryNetworking;

@Mod("ae2categories")
public final class CategoriesMod {
    public static boolean enabled = true;
    public CategoriesMod(IEventBus bus) {
        ControllerRegistry.register(bus);
        bus.addListener(CategoryNetworking::register);
        NeoForge.EVENT_BUS.addListener(CategoryNetworking::tick);
    }
}
