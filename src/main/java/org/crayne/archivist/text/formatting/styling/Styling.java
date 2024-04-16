package org.crayne.archivist.text.formatting.styling;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Styling {

    @NotNull
    private final Set<StylingVariant> stylingVariants;

    private Styling(@NotNull final Collection<StylingVariant> stylingVariants) {
        this.stylingVariants = Set.copyOf(stylingVariants);
    }

    @NotNull
    public String legacyStyling(final char colorCodeChar) {
        return stylingVariants.stream()
                .map(v -> v.legacyStyling(colorCodeChar))
                .collect(Collectors.joining());
    }

    @NotNull
    public Styling merge(@NotNull final Styling other) {
        final Set<StylingVariant> result = new HashSet<>(stylingVariants);
        result.addAll(other.stylingVariants);
        return new Styling(result);
    }

    @NotNull
    public static Styling none() {
        return styling();
    }

    @NotNull
    public static Styling styling(@NotNull final StylingVariant @NotNull ... variants) {
        return Styling.styling(Arrays.stream(variants).collect(Collectors.toSet()));
    }

    @NotNull
    public static Styling styling(@NotNull final Collection<StylingVariant> variants) {
        return new Styling(variants);
    }

    public boolean empty() {
        return stylingVariants.isEmpty();
    }

    public boolean is(@NotNull final StylingVariant variant) {
        return stylingVariants.contains(variant);
    }

    public boolean reset() {
        return is(StylingVariant.RESET);
    }

    public boolean bold() {
        return is(StylingVariant.BOLD);
    }

    public boolean italic() {
        return is(StylingVariant.ITALIC);
    }

    public boolean underlined() {
        return is(StylingVariant.UNDERLINED);
    }

    public boolean strikethrough() {
        return is(StylingVariant.STRIKETHROUGH);
    }

    public boolean obfuscated() {
        return is(StylingVariant.OBFUSCATED);
    }

    @NotNull
    public Set<StylingVariant> stylingVariants() {
        return stylingVariants;
    }

    @NotNull
    public String toString() {
        return "Styling(" +
                stylingVariants +
                ')';
    }

}
