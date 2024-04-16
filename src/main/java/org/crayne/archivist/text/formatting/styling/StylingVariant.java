package org.crayne.archivist.text.formatting.styling;

import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum StylingVariant {

    RESET,
    BOLD,
    ITALIC,
    UNDERLINED,
    STRIKETHROUGH,
    OBFUSCATED;

    @NotNull
    public String legacyStyling(final char colorCodeChar) {
        return switch (this) {
            case RESET -> colorCodeChar + "r";
            case BOLD -> colorCodeChar + "l";
            case ITALIC -> colorCodeChar + "o";
            case UNDERLINED -> colorCodeChar + "n";
            case STRIKETHROUGH -> colorCodeChar + "m";
            case OBFUSCATED -> colorCodeChar + "k";
        };
    }

    @Nullable
    public TextDecoration adventureTextDecoration() {
        return switch (this) {
            case BOLD -> TextDecoration.BOLD;
            case ITALIC -> TextDecoration.ITALIC;
            case UNDERLINED -> TextDecoration.UNDERLINED;
            case STRIKETHROUGH -> TextDecoration.STRIKETHROUGH;
            case OBFUSCATED -> TextDecoration.OBFUSCATED;
            case RESET -> null;
        };
    }

}
