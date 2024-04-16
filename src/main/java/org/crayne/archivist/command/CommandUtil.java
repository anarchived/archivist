package org.crayne.archivist.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class CommandUtil {

    private CommandUtil() {

    }

    @NotNull
    public static Component errorMessage(@NotNull final String text) {
        return Component.text(text).color(TextColor.color(128, 30, 0));
    }

}
