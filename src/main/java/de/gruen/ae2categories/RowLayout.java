package de.gruen.ae2categories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Display-only positions; null items never enter the storage repository. */
public final class RowLayout {
    public record Cell<T>(String header, T item) {}

    public static <T> List<Cell<T>> build(List<T> entries, int columns,
            List<String> order, Function<T, String> category) {
        return build(entries, columns, order, category, Set.of());
    }

    public static <T> List<Cell<T>> build(List<T> entries, int columns,
            List<String> order, Function<T, String> category, Set<String> collapsed) {
        if (columns < 1) throw new IllegalArgumentException("columns");
        var groups = new LinkedHashMap<String, List<T>>();
        for (String name : order) groups.put(name, new ArrayList<>());
        for (T entry : entries) groups.computeIfAbsent(category.apply(entry), k -> new ArrayList<>()).add(entry);
        var result = new ArrayList<Cell<T>>();
        groups.forEach((name, items) -> {
            if (items.isEmpty()) return;
            for (int i = 0; i < columns; i++) result.add(new Cell<>(name, null));
            if (collapsed.contains(name)) return;
            for (T item : items) result.add(new Cell<>(null, item));
            while (result.size() % columns != 0) result.add(new Cell<>(null, null));
        });
        return result;
    }
}
