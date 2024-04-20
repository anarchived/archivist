package org.crayne.archivist.index.blob.region;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Optional;

public record Region(int regionX, int regionZ, @NotNull WorldType worldType, @NotNull Path source) {

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Region region = (Region) o;

        if (regionX != region.regionX) return false;
        if (regionZ != region.regionZ) return false;
        return worldType == region.worldType;
    }

    @NotNull
    public static Optional<Region> parse(@NotNull final Path path, @NotNull final WorldType worldType) {
        final String fileName = path.getFileName().toString();

        if (!fileName.startsWith("r.") || !fileName.endsWith(".mca"))
            return Optional.empty();

        // region filenames follow the format r.X.Z.mca, so we only care about indices 1 and 2 when splitting by '.'
        final String[] split = fileName.split("\\.");
        final String regionXStr = split[1];
        final String regionZStr = split[2];

        final int regionX, regionZ;
        try {
            regionX = Integer.parseInt(regionXStr);
            regionZ = Integer.parseInt(regionZStr);
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(new Region(regionX, regionZ, worldType, path));
    }

    public int hashCode() {
        int result = regionX;
        result = 31 * result + regionZ;
        result = 31 * result + worldType.hashCode();
        return result;
    }

    @NotNull
    public String toString() {
        return source.getParent().getFileName() + ":r." + regionX + "." + regionZ + ".mca";
    }

}
