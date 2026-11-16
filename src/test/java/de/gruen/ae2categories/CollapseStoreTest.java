package de.gruen.ae2categories;

import de.gruen.ae2categories.client.CollapseStore;
import java.nio.file.Path;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class CollapseStoreTest {
    @TempDir Path directory;
    @Test void closedAndReopenedStateSurvivesReload() throws Exception {
        var path = directory.resolve("prefs.json");
        var store = new CollapseStore(path);
        store.setCollapsed("world", "controller-uuid", true);
        assertTrue(new CollapseStore(path).collapsed("world").contains("controller-uuid"));
        store.setCollapsed("world", "controller-uuid", false);
        assertTrue(new CollapseStore(path).collapsed("world").isEmpty());
    }
    @Test void worldsAndControllersRemainIndependent() throws Exception {
        var store = new CollapseStore(directory.resolve("prefs.json"));
        store.setCollapsed("world-a", "uuid-1", true);
        store.setCollapsed("world-a", "__other__", true);
        assertFalse(store.collapsed("world-a").contains("uuid-2"));
        assertTrue(store.collapsed("world-b").isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> store.collapsed("world-a").clear());
    }
    @Test void unreadableJsonIsNotSilentlyOverwritten() throws Exception {
        var path = directory.resolve("prefs.json");
        Files.writeString(path, "{ broken json");
        assertThrows(Exception.class, () -> new CollapseStore(path));
        assertEquals("{ broken json", Files.readString(path));
    }
}
