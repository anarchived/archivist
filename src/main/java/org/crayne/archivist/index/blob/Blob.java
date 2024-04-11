package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.region.Dimension;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Blob {

    @NotNull
    private final Map<UUID, Region> regions;

    @NotNull
    private final Dimension dimension;

    public Blob(@NotNull final Collection<Region> regions, @NotNull final Dimension dimension) {
        this.dimension = dimension;
        verifyRegionDimensions(regions);

        this.regions = new HashMap<>();
        regions.forEach(region -> this.regions.put(UUID.randomUUID(), region));
    }

    public Blob(@NotNull final Dimension dimension) {
        this(Collections.emptySet(), dimension);
    }

    private void verifyRegionDimensions(@NotNull final Collection<Region> regions) {
        for (final Region region : regions) {
            if (dimension != region.dimension())
                throw new IndexingException("Cannot add regions of different dimension to a blob");
        }
    }

    @NotNull
    public Dimension dimension() {
        return dimension;
    }

    @NotNull
    public Collection<Region> regions() {
        return regions.values();
    }

    public boolean hasRegion(@NotNull final UUID uuid) {
        return regions.containsKey(uuid);
    }

    @NotNull
    public Optional<UUID> findAnyRegionUUID() {
        return regions.keySet().stream().findAny();
    }

    public boolean mergeRegions(@NotNull final Blob blob) {
        if (anyContained(blob)) return false;
        regions.putAll(blob.regions);
        return true;
    }

    public boolean anyContained(@NotNull final Blob other) {
        return other.regions().stream().anyMatch(regions()::contains);
    }

}
