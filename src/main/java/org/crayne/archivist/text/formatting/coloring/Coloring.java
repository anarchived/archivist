package org.crayne.archivist.text.formatting.coloring;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

public class Coloring {

    private final int r, g, b;
    private final int value;

    private Coloring(final int r, final int g, final int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.value = (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    private Coloring(final int value) {
        this.r = value >> 16 & 255;
        this.g = value >> 8 & 255;
        this.b = value & 255;
        this.value = value;
    }

    @NotNull
    public TextColor adventureTextColor() {
        return TextColor.color(value);
    }

    @NotNull
    public String legacyColoring(final char colorCodeChar) {
        final Optional<Character> baseCase = Optional.ofNullable(NamedColoring.NAMED_COLORS_CODES.get(this));
        return baseCase
                .map(c -> "" + colorCodeChar + c)
                .orElse(legacyColoringHexCode(colorCodeChar));
    }

    @NotNull
    public String legacyColoringHexCode(final char colorCodeChar) {
        return colorCodeChar + "x" + Integer.toHexString(value)
                .chars()
                .mapToObj(i -> "" + colorCodeChar + (char) i)
                .collect(Collectors.joining());
    }

    public int r() {
        return r;
    }

    public int g() {
        return g;
    }

    public int b() {
        return b;
    }

    public int value() {
        return value;
    }

    @NotNull
    public static Coloring rgb(final int r, final int g, final int b) {
        return new Coloring(r, g, b);
    }

    @NotNull
    public static Coloring rgb(final int value) {
        return new Coloring(value);
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Coloring coloring = (Coloring) o;

        if (r != coloring.r) return false;
        if (g != coloring.g) return false;
        if (b != coloring.b) return false;
        return value == coloring.value;
    }

    public int hashCode() {
        int result = r;
        result = 31 * result + g;
        result = 31 * result + b;
        result = 31 * result + value;
        return result;
    }

    @NotNull
    public String toString() {
        return Optional.ofNullable(NamedColoring.NAMED_COLORS.get(this))
                .orElse("RGB(" +
                        r +
                        ", " + g +
                        ", " + b +
                        ')'
                );
    }

}