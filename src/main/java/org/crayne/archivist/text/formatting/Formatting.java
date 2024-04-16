package org.crayne.archivist.text.formatting;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.crayne.archivist.text.formatting.coloring.Coloring;
import org.crayne.archivist.text.formatting.styling.Styling;
import org.crayne.archivist.text.formatting.styling.StylingVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Formatting {

    @Nullable
    private final Coloring coloring;

    @NotNull
    private final Styling styling;

    private Formatting(@Nullable Coloring coloring, @NotNull Styling styling) {
        this.coloring = coloring;
        this.styling = styling;
    }

    @Nullable
    public TextColor adventureTextColor() {
        return coloring()
                .map(Coloring::adventureTextColor)
                .orElse(null);
    }

    @NotNull
    public TextDecoration @NotNull [] adventureTextDecorations() {
        return styling.stylingVariants()
                .stream()
                .map(StylingVariant::adventureTextDecoration)
                .filter(Objects::nonNull)
                .toList()
                .toArray(TextDecoration[]::new);
    }

    @NotNull
    public String legacyFormatting(final char colorCodeChar) {
        return coloring().map(c -> c.legacyColoring(colorCodeChar)).orElse("")
                + styling.legacyStyling(colorCodeChar);
    }

    @NotNull
    public Optional<Coloring> coloring() {
        return Optional.ofNullable(coloring);
    }

    @NotNull
    public Styling styling() {
        return styling;
    }

    @NotNull
    public Formatting merge(@NotNull final Formatting other) {
        if (other.styling().reset()) return Formatting.none();

        return new Formatting(
                other.coloring().or(this::coloring).orElse(null),
                other.styling().merge(styling())
        );
    }

    public boolean empty() {
        return coloring == null && styling().empty();
    }

    @NotNull
    public static Formatting none() {
        return none(false);
    }

    @NotNull
    public static Formatting none(final boolean reset) {
        return new Formatting(null, Styling.styling(
                reset ? List.of(StylingVariant.RESET) : Collections.emptyList())
        );
    }

    @NotNull
    public static Formatting colored(@NotNull final Coloring coloring) {
        return new Formatting(coloring, Styling.none());
    }

    @NotNull
    public static Formatting styled(@NotNull final Styling styling) {
        return new Formatting(null, styling);
    }

    @NotNull
    public static Formatting styled(@NotNull final StylingVariant @NotNull ... stylingVariants) {
        return styled(Styling.styling(stylingVariants));
    }

    @NotNull
    public static Formatting formatted(@NotNull final Coloring coloring, @NotNull final Styling styling) {
        return new Formatting(coloring, styling);
    }

    @NotNull
    public static Formatting formatted(@NotNull final Coloring coloring, @NotNull final StylingVariant @NotNull ... stylingVariants) {
        return new Formatting(coloring, Styling.styling(stylingVariants));
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Formatting that = (Formatting) o;

        if (!Objects.equals(coloring, that.coloring)) return false;
        return styling == that.styling;
    }

    public int hashCode() {
        int result = coloring != null ? coloring.hashCode() : 0;
        result = 31 * result + styling.hashCode();
        return result;
    }

    @NotNull
    public String toString() {
        return "Formatting(" +
                coloring().map(Coloring::toString).orElse("NONE") +
                ", " + styling +
                ')';
    }


}