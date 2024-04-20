package org.crayne.archivist.gui.util;

import org.crayne.archivist.inventory.ArchivistInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class LoreUtil {

    private LoreUtil() {

    }


    public static void addRemainingLines(@NotNull final List<String> lore, @NotNull final Collection<String> add) {
        int i = 0;
        for (final String line : add) {
            if (i >= 8) break;

            lore.add(ArchivistInventory.secondaryText(line).legacyText());
            i++;
        }
        final int remaining = add.size() - 8;
        if (remaining > 0) lore.add(ArchivistInventory.mainText("...and " + remaining + " more").legacyText());
    }

}
