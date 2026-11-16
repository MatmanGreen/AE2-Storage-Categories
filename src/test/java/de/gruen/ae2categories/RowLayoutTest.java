package de.gruen.ae2categories;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class RowLayoutTest {
    @Test void collapsedGroupKeepsHeadingAndMovesFollowingItemsToCorrectRow() {
        var entries = List.of("a1", "a2", "b1");
        var cells = RowLayout.build(entries, 3, List.of("a", "b"), s -> s.substring(0, 1), Set.of("a"));
        assertEquals(9, cells.size());
        assertEquals("a", cells.get(0).header());
        assertEquals("b", cells.get(3).header());
        assertEquals("b1", cells.get(6).item());
        assertEquals(List.of("b1"), cells.stream().map(RowLayout.Cell::item).filter(java.util.Objects::nonNull).toList());
        var expanded = RowLayout.build(entries, 3, List.of("a", "b"), s -> s.substring(0, 1), Set.of());
        assertEquals(entries, expanded.stream().map(RowLayout.Cell::item).filter(java.util.Objects::nonNull).toList());
    }
    @Test void allCollapsedStillScrollableAsHeaderRowsAndEmptyGroupsDisappear() {
        var cells = RowLayout.build(List.of("a1", "b1"), 9, List.of("a", "b", "empty"),
                s -> s.substring(0, 1), Set.of("a", "b", "empty"));
        assertEquals(18, cells.size());
        assertTrue(cells.stream().allMatch(c -> c.item() == null && c.header() != null));
    }
    @Test void separatesHeadersPaddingAndItemsWithoutDuplicates() {
        var cells = RowLayout.build(List.of("a1", "b1", "a2", "a3", "a4"), 3,
                List.of("a", "empty", "b"), s -> s.substring(0, 1));
        assertEquals(15, cells.size());
        assertEquals(List.of("a1", "a2", "a3", "a4", "b1"),
                cells.stream().map(RowLayout.Cell::item).filter(java.util.Objects::nonNull).toList());
        for (int i = 0; i < 3; i++) { assertEquals("a", cells.get(i).header()); assertNull(cells.get(i).item()); }
        assertNull(cells.get(7).item());
        assertEquals("b", cells.get(9).header());
    }
    @Test void emptyInventoryHasNoHeadings() {
        assertTrue(RowLayout.build(List.<String>of(), 9, List.of("a"), s -> "a").isEmpty());
    }
    @Test void supportsDifferentWidthsAndKeepsEveryItem() {
        for (int columns : List.of(1, 9, 12)) {
            var cells = RowLayout.build(List.of(1, 2, 3), columns, List.of("x"), n -> "x");
            assertEquals(0, cells.size() % columns);
            assertEquals(3, cells.stream().filter(c -> c.item() != null).count());
        }
    }
}
