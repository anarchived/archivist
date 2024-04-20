package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.blob.region.WorldType;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class BlobField {

    @NotNull
    private final Map<WorldType, BlobWorld> blobWorldMap;

    @NotNull
    private final String serverTitle;

    public BlobField(@NotNull final String serverTitle) {
        this.blobWorldMap = new HashMap<>();
        this.serverTitle = serverTitle;
    }

    @NotNull
    public String serverTitle() {
        return serverTitle;
    }

    public void merge(@NotNull final Blob blob) {
        final WorldType worldType = blob.world();
        final BlobWorld blobWorld = blobWorldMap.getOrDefault(worldType, new BlobWorld(worldType));
        blobWorld.merge(blob);
        blobWorldMap.put(worldType, blobWorld);
    }

    @NotNull
    public Optional<BlobLevel> findBlobLevel(@NotNull final Path path) {
        for (final WorldType worldType : blobWorldMap.keySet()) {
            final BlobWorld blobWorld = blobWorldMap.get(worldType);
            final int blobIndex = blobWorld.findBlobIndex(path);
            if (blobIndex == -1) continue;

            return Optional.of(new BlobLevel(blobIndex, worldType, serverTitle));
        }
        return Optional.empty();
    }

    @NotNull
    public Map<WorldType, BlobWorld> blobWorlds() {
        return Collections.unmodifiableMap(blobWorldMap);
    }
    
    @NotNull
    public Map<BlobLevel, Collection<Region>> blobRegions() {
        final Map<BlobLevel, Collection<Region>> result = new HashMap<>();
        
        for (final WorldType worldType : blobWorldMap.keySet()) {
            final BlobWorld blobWorld = blobWorldMap.get(worldType);
            blobWorld.blobs().forEach((index, blob) ->
                    result.put(new BlobLevel(index, worldType, serverTitle), blob.regions()));
        }
        return result;
    }

    @NotNull
    public Set<BlobLevel> blobLevels() {
        return blobWorlds()
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
                "blobWorldMap=" + blobWorldMap +
                '}';
    }
}
