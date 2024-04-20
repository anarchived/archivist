package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.region.WorldType;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class Blob {

    @NotNull
    private final Map<Path, Region> regions;

    @NotNull
    private final WorldType worldType;

    public Blob(@NotNull final Collection<Region> regions, @NotNull final WorldType worldType) {
        this.worldType = worldType;
        verifyRegionWorlds(regions);

        this.regions = new HashMap<>();
        regions.forEach(region -> this.regions.put(region.source(), region));
    }

    public Blob(@NotNull final WorldType worldType) {
        this(Collections.emptySet(), worldType);
    }

    private void verifyRegionWorlds(@NotNull final Collection<Region> regions) {
        for (final Region region : regions) {
            if (worldType != region.worldType())
                throw new IndexingException("Cannot add regions of different worldType to a blob");
        }
    }

    @NotNull
    public WorldType world() {
        return worldType;
    }

    @NotNull
    public Collection<Region> regions() {
        return regions.values();
    }

    public boolean hasRegion(@NotNull final Path path) {
        return regions.keySet()
                .stream()
                .anyMatch(regionPath -> regionPath.getParent().equals(path));
    }

    public boolean mergeRegions(@NotNull final Blob blob) {
        if (anyContained(blob)) return false;

        regions.putAll(blob.regions);
        return true;
    }

    public boolean anyContained(@NotNull final Blob other) {
        return other.regions().stream().anyMatch(regions()::contains);
    }

    @NotNull
    public String toString() {
        return "Blob{" +
                "regions=" + regions +
                ", worldType=" + worldType +
                '}';
    }

}
