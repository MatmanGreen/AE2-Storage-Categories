package de.gruen.ae2categories.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Client preferences, separate from world/controller data. */
public final class CollapseStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;
    private final Map<String, Set<String>> worlds = new HashMap<>();

    public CollapseStore(Path path) throws IOException {
        this.path = path;
        if (Files.exists(path)) {
            try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                Map<String, Set<String>> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Set<String>>>() {}.getType());
                if (loaded != null) loaded.forEach((world, keys) -> {
                    if (world != null && keys != null) {
                        var valid = new HashSet<String>();
                        for (var key : keys) if (key != null) valid.add(key);
                        worlds.put(world, valid);
                    }
                });
            }
        }
    }
    public Set<String> collapsed(String world) { return Set.copyOf(worlds.getOrDefault(world, Set.of())); }
    public void setCollapsed(String world, String categoryId, boolean collapsed) throws IOException {
        var keys = worlds.computeIfAbsent(world, key -> new HashSet<>());
        if (collapsed) keys.add(categoryId); else keys.remove(categoryId);
        if (keys.isEmpty()) worlds.remove(world);
        Files.createDirectories(path.toAbsolutePath().getParent());
        Path temporary = Files.createTempFile(path.toAbsolutePath().getParent(), "categories-", ".tmp");
        try {
            Files.writeString(temporary, GSON.toJson(worlds), StandardCharsets.UTF_8);
            try { Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temporary); }
    }
}
