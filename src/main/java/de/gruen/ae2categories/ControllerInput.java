package de.gruen.ae2categories;

import java.util.OptionalInt;

public final class ControllerInput {
    public static OptionalInt priority(String text) {
        try {
            if (!text.matches("-?\\d{1,7}")) return OptionalInt.empty();
            int value = Integer.parseInt(text);
            return value >= -1000000 && value <= 1000000 ? OptionalInt.of(value) : OptionalInt.empty();
        } catch (NumberFormatException e) { return OptionalInt.empty(); }
    }
}
