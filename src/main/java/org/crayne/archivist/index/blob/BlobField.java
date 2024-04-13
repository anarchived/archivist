package org.crayne.archivist.index.blob;

import org.crayne.archivist.index.blob.region.World;
import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class BlobField {

    @NotNull
    private final Map<World, BlobWorld> blobWorldMap;

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
        final World world = blob.world();
        final BlobWorld blobWorld = blobWorldMap.getOrDefault(world, new BlobWorld(world));
        blobWorld.merge(blob);
        blobWorldMap.put(world, blobWorld);
    }

    @NotNull
    public Optional<BlobLevel> findBlobLevel(@NotNull final UUID regionUUID) {
        for (final World world : blobWorldMap.keySet()) {
            final BlobWorld blobWorld = blobWorldMap.get(world);
            final int blobIndex = blobWorld.findBlobIndex(regionUUID);
            if (blobIndex == -1) continue;

            return Optional.of(new BlobLevel(blobIndex, world, serverTitle));
        }
        return Optional.empty();
    }

    @NotNull
    public Optional<BlobLevel> findBlobLevel(@NotNull final SaveIdentifier saveIdentifier) {
        return findBlobLevel(saveIdentifier.uuid());
    }

    @NotNull
    public Map<World, BlobWorld> blobWorlds() {
        return Collections.unmodifiableMap(blobWorldMap);
    }
    
    @NotNull
    public Map<BlobLevel, Collection<Region>> blobRegions() {
        final Map<BlobLevel, Collection<Region>> result = new HashMap<>();
        
        for (final World world : blobWorldMap.keySet()) {
            final BlobWorld blobWorld = blobWorldMap.get(world);
            blobWorld.blobs().forEach((index, blob) ->
                    result.put(new BlobLevel(index, world, serverTitle), blob.regions()));
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
