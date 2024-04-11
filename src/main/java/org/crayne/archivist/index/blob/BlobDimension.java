package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.region.Dimension;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlobDimension {

    @NotNull
    private final Map<Integer, Blob> blobs;

    @NotNull
    private final Dimension dimension;

    public BlobDimension(@NotNull final Dimension dimension) {
        this.blobs = new HashMap<>();
        this.dimension = dimension;
    }

    public void merge(@NotNull final Blob blob) {
        if (blob.dimension() != dimension)
            throw new IndexingException("Cannot merge blobs of different dimensions together");

        int i = 0;
        for (; i < blobs.size(); i++) {
            final Blob nextBlob = blobs.getOrDefault(i, new Blob(blob.dimension()));

            // if the blob can be merged, merge it into the lowest level possible and return
            if (nextBlob.mergeRegions(blob)) return;
        }
        // if there was no previous blob to merge the next blob into, simply create a new level
        blobs.put(i, blob);
    }

    public int findBlobIndex(@NotNull final UUID regionUUID) {
        for (int level : blobs.keySet()) {
            final Blob blob = blobs.get(level);
            if (blob.hasRegion(regionUUID)) return level;
        }
        return -1;
    }

    @NotNull
    public Dimension dimension() {
        return dimension;
    }

    @NotNull
    public Map<Integer, Blob> blobs() {
        return Collections.unmodifiableMap(blobs);
    }

    @NotNull
    public String toString() {
        return "BlobDimension{" +
                "blobs=" + blobs +
                ", dimension=" + dimension +
                '}';
    }
}
