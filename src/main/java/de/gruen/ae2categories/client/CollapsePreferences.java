package de.gruen.ae2categories.client;

import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLPaths;

public final class CollapsePreferences {
    private static CollapseStore store;
    private static boolean initialized;
    private static final Map<String, Set<String>> fallback = new HashMap<>();

    private static void init() {
        if (initialized) return;
        initialized = true;
        try { store = new CollapseStore(FMLPaths.CONFIGDIR.get().resolve("ae2categories-collapse.json")); }
        catch (Exception e) { LogUtils.getLogger().warn("Cannot read category preferences; keeping this session in memory and preserving the existing file", e); }
    }
    private static String scope() {
        var mc = Minecraft.getInstance();
        var server = mc.getSingleplayerServer();
        String world = server != null ? "local:" + server.getWorldPath(LevelResource.ROOT).toAbsolutePath().normalize()
                : mc.getCurrentServer() != null ? "server:" + mc.getCurrentServer().ip : "session";
        String player = mc.player == null ? "unknown" : mc.player.getUUID().toString();
        return UUID.nameUUIDFromBytes((world + "|" + player).getBytes(StandardCharsets.UTF_8)).toString();
    }
    public static Set<String> collapsed() {
        init();
        return store == null ? Set.copyOf(fallback.getOrDefault(scope(), Set.of())) : store.collapsed(scope());
    }
    public static void setCollapsed(String id, boolean collapsed) {
        init();
        if (store == null) {
            var keys = fallback.computeIfAbsent(scope(), k -> new HashSet<>());
            if (collapsed) keys.add(id); else keys.remove(id);
            return;
        }
        try { store.setCollapsed(scope(), id, collapsed); }
        catch (Exception e) { LogUtils.getLogger().warn("Cannot save category preferences; the current session still remembers the state", e); }
    }
}
