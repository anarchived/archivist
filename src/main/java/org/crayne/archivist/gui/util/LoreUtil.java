package org.crayne.archivist.gui.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoreUtil {

    private LoreUtil() {

    }


    public static void addRemainingLines(@NotNull final List<String> lore, @NotNull final List<String> add) {
        for (int i = 0; i < 8 && i < add.size(); i++) {
            lore.add(ChatColor.GRAY + add.get(i));
        }
        final int remaining = add.size() - 8;
        if (remaining > 0) lore.add(ChatColor.YELLOW + "...and " + remaining + "more");
    }

}
