package org.crayne.archivist.index.cache;

import org.bukkit.Location;
import org.bukkit.World;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.Index;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.BlobField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class IndexCache {

    @NotNull
    private final Map<String, ServerCache> serverCacheMap;

    @Nullable
    private Map<World, ServerCache> blobCache;

    public IndexCache() {
        this.serverCacheMap = new HashMap<>();
    }

    public void load() {
        ArchivistPlugin.log("Loading archive index", Level.INFO);
        final Index archivedServerSaves = archivedServerSaves();

        archivedServerSaves.children().forEach(server -> {
            ArchivistPlugin.log("Loading server index " + server.title(), Level.INFO);
            final ServerCache serverCache = new ServerCache(server);
            serverCache.load();
            serverCacheMap.put(serverCache.name(), serverCache);
        });
    }

    @NotNull
    public Set<World> collectBlobs() {
        return collectBlobWorldMap().keySet();
    }

    @NotNull
    public Map<World, ServerCache> collectBlobWorldMap() {
        if (blobCache != null && !blobCache.isEmpty()) return blobCache;

        blobCache = serverCacheMap.values()
                .stream()
                .flatMap(serverCache ->
                        serverCache.saveCacheMap()
                                .values()
                                .stream()
                                .flatMap(saveCache -> saveCache.variants()
                                        .values()
                                        .stream())
                                .map(world -> Map.entry(world, serverCache)))
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return blobCache;
    }

    public void copyRegionFiles() {
        ArchivistPlugin.log("Copying region files", Level.INFO);
        serverCacheMap.values().forEach(ServerCache::copyRegionFiles);
    }

    public void loadAllVariants() {
        serverCacheMap.values().forEach(serverCache -> {
            final Optional<BlobField> blobField = serverCache.blobField();
            if (blobField.isEmpty()) return;

            serverCache.saveCacheMap()
                    .values()
                    .forEach(save -> save.loadVariants(blobField.get()));
        });
    }

    public void loadAllTags() {
        serverCacheMap.values().forEach(serverCache -> serverCache
                .saveCacheMap()
                .values()
                .forEach(SaveCache::loadTags)
        );
    }

    @NotNull
    public Optional<ServerCache> resolveServerCache(@NotNull final String server) {
        return Optional.ofNullable(serverCacheMap.get(server));
    }

    @NotNull
    public Optional<Location> resolveSaveLocation(@NotNull final String server,
                                                  @NotNull final String save, @NotNull final String variant) {
        return resolveServerCache(server)
                .flatMap(serverCache -> serverCache.resolveSaveLocation(save, variant));
    }

    @NotNull
    public Location requireSaveLocation(@NotNull final String server,
                                        @NotNull final String save, @NotNull final String variant) {
        return resolveSaveLocation(server, save, variant)
                .orElseThrow(() -> new IndexingException("Cannot find a teleport location for " + server + ": " + save + "-" + variant));
    }

    @NotNull
    public Map<String, ServerCache> serverCacheMap() {
        return Collections.unmodifiableMap(serverCacheMap);
    }

    @NotNull
    public static Path rootDirectory() {
        return Path.of(ArchivistPlugin.instance().getDataFolder().toURI()).getParent().getParent();
    }

    @NotNull
    public static Path archivesDirectory() {
        return rootDirectory().resolve("archives");
    }

    @NotNull
    public static Index archivedServerSaves() {
        return new Index(archivesDirectory().resolve("base-archive"));
    }

    @NotNull
    public static Index archivedServerMaps() {
        return new Index(archivesDirectory().resolve("map-archive"));
    }

}
