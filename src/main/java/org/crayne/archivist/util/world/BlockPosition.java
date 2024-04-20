package org.crayne.archivist.util.world;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public record BlockPosition(int x, int y, int z) {

    @NotNull
    public static BlockPosition of(@NotNull final Location location) {
        return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}