package org.crayne.archivist.index.blob.region;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public record Region(int regionX, int regionZ, @NotNull World world, @NotNull Path source) {

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Region region = (Region) o;

        if (regionX != region.regionX) return false;
        if (regionZ != region.regionZ) return false;
        return world == region.world;
    }

    public int hashCode() {
        int result = regionX;
        result = 31 * result + regionZ;
        result = 31 * result + world.hashCode();
        return result;
    }

    @NotNull
    public String toString() {
        return "Region{" +
                "regionX=" + regionX +
                ", regionZ=" + regionZ +
                ", world=" + world +
                '}';
    }

}
