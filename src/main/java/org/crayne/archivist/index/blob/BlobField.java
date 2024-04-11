package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.blob.region.Dimension;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BlobField {

    @NotNull
    private final Map<Dimension, BlobDimension> blobDimensionMap;

    @NotNull
    private final String serverTitle;

    public BlobField(@NotNull final String serverTitle) {
        this.blobDimensionMap = new HashMap<>();
        this.serverTitle = serverTitle;
    }

    @NotNull
    public String serverTitle() {
        return serverTitle;
    }

    public void merge(@NotNull final Blob blob) {
        final Dimension dimension = blob.dimension();
        final BlobDimension blobDimension = blobDimensionMap.getOrDefault(dimension, new BlobDimension(dimension));
        blobDimension.merge(blob);
        blobDimensionMap.put(dimension, blobDimension);
    }

    @NotNull
    public Optional<BlobLevel> findBlobLevel(@NotNull final UUID regionUUID) {
        for (final Dimension dimension : blobDimensionMap.keySet()) {
            final BlobDimension blobDimension = blobDimensionMap.get(dimension);
            final int blobIndex = blobDimension.findBlobIndex(regionUUID);
            if (blobIndex == -1) continue;

            return Optional.of(new BlobLevel(blobIndex, dimension, serverTitle));
        }
        return Optional.empty();
    }

    @NotNull
    public Optional<BlobLevel> findBlobLevel(@NotNull final SaveIdentifier saveIdentifier) {
        return findBlobLevel(saveIdentifier.uuid());
    }

    @NotNull
    public Map<Dimension, BlobDimension> blobDimensions() {
        return Collections.unmodifiableMap(blobDimensionMap);
    }
    
    @NotNull
    public Map<BlobLevel, Collection<Region>> blobRegions() {
        final Map<BlobLevel, Collection<Region>> result = new HashMap<>();
        
        for (final Dimension dimension : blobDimensionMap.keySet()) {
            final BlobDimension blobDimension = blobDimensionMap.get(dimension);
            blobDimension.blobs().forEach((index, blob) ->
                    result.put(new BlobLevel(index, dimension, serverTitle), blob.regions()));
        }
        return result;
    }

    @NotNull
    public Set<BlobLevel> blobLevels() {
        return blobDimensions()
                .entrySet()
                .stream()
                .flatMap(e -> e.getValue()
                        .blobs()
                        .keySet()
                        .stream()
                        .map(i -> new BlobLevel(i, e.getKey(), serverTitle))
                )
                .collect(Collectors.toSet());
    }

    @NotNull
    public String toString() {
        return "BlobField{" +
                "blobDimensionMap=" + blobDimensionMap +
                '}';
    }
}
