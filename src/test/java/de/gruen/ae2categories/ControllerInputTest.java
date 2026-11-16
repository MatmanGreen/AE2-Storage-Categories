package de.gruen.ae2categories;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControllerInputTest {
    @Test void acceptsExactBoundsAndNegativeValues() {
        for (int value : new int[]{-1000000, -1, 0, 1, 1000000})
            assertEquals(value, ControllerInput.priority(Integer.toString(value)).orElseThrow());
    }
    @Test void rejectsIncompleteOutOfRangeAndInvalidInputInsteadOfChangingIt() {
        for (String text : new String[]{"", "-", "1000001", "-1000001", "9999999", "1.5", "abc", " 1", "99999999999999"})
            assertTrue(ControllerInput.priority(text).isEmpty(), text);
    }
}
