package org.crayne.archivist.index.tags;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public enum UnaryTag {

    EXPLICIT_CONTENT( "Explicit Content"), // may include explicit content, such as sexual map art
    HATEFUL_CONTENT("Hateful Content"), // may include hate speech or hateful symbols / builds (swastikas, etc)
    INCOMPLETE_INFORMATION("Incomplete Information"), // does not include full information about the base
    INCOMPLETE_WORLD_DOWNLOAD("Incomplete World Download"), // does not include all parts of the base, or has missing chunks
    MISSING_ARCHIVALS("Missing Archivals"), // not all archivals are included, such as (un)griefed variants and other historic timestamps
    HAS_CONTAINER_DATA("Has Container Data"),
    HAS_ENTITY_DATA("Has Entity Data"),
    EST("Est."), // establishment date / year of the base
    ICON("Icon", true), // icon in the browser menu
    GLINT("Glint", true), // makes the icon in browser menu glint if this tag was attached
    ;

    @NotNull
    private final String asString;

    private final boolean hidden;

    UnaryTag(@NotNull final String asString) {
        this(asString, false);
    }

    UnaryTag(@NotNull final String asString, final boolean hidden) {
        this.asString = asString;
        this.hidden = hidden;
    }

    public boolean hidden() {
        return hidden;
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
