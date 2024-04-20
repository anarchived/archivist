package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.region.WorldType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlobWorld {

    @NotNull
    private final Map<Integer, Blob> blobs;

    @NotNull
    private final WorldType worldType;

    public BlobWorld(@NotNull final WorldType worldType) {
        this.blobs = new HashMap<>();
        this.worldType = worldType;
    }

    public void merge(@NotNull final Blob blob) {
        if (blob.world() != worldType)
            throw new IndexingException("Cannot merge blobs of different worlds together");

        int i = 0;
        for (; i < blobs.size(); i++) {
            final Blob nextBlob = blobs.getOrDefault(i, new Blob(blob.world()));

            // if the blob can be merged, merge it into the lowest level possible and return
            if (nextBlob.mergeRegions(blob)) return;
        }
        // if there was no previous blob to merge the next blob into, simply create a new level
        blobs.put(i, blob);
    }

    public int findBlobIndex(@NotNull final Path path) {
        for (int level : blobs.keySet()) {
            final Blob blob = blobs.get(level);
            if (blob.hasRegion(path)) return level;
        }
        return -1;
    }

    @NotNull
    public WorldType world() {
        return worldType;
    }

    @NotNull
    public Map<Integer, Blob> blobs() {
        return Collections.unmodifiableMap(blobs);
    }

    @NotNull
    public String toString() {
        return "BlobWorld{" +
                "blobs=" + blobs +
                ", worldType=" + worldType +
                '}';
    }
}
