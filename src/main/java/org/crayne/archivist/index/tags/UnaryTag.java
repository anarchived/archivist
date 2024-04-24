package org.crayne.archivist.index.tags;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum UnaryTag {

    EXPLICIT_CONTENT( "Explicit Content"), // may include explicit content, such as sexual map art
    HATEFUL_CONTENT("Hateful Content"), // may include hate speech or hateful symbols / builds (swastikas, etc)
    INCOMPLETE_INFORMATION("Incomplete Information"), // does not include full information about the base
    INCOMPLETE_WORLD_DOWNLOAD("Incomplete World-download"), // does not include all parts of the base, or has missing chunks
    MISSING_ARCHIVALS("Missing Archivals"), // not all archivals are included, such as (un)griefed variants and other historic timestamps
    EST("Est."); // establishment date / year of the base

    @NotNull
    private final String asString;

    UnaryTag(@NotNull final String asString) {
        this.asString = asString;
    }

    @NotNull
    public String toString() {
        return asString;
    }

    @NotNull
    private static String sanitize(@NotNull final String text) {
        return text.replaceAll("[ _.]", "");
    }

    @NotNull
    public static Optional<UnaryTag> parse(@NotNull final String text) {
        return Arrays.stream(values())
                .filter(t -> sanitize(t.toString()).equalsIgnoreCase(sanitize(text)))
                .findAny();
    }

}
