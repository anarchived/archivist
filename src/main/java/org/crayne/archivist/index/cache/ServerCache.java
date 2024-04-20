package org.crayne.archivist.index.cache;

import org.bukkit.Location;
import org.bukkit.World;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.Index;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.BlobLevel;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import static org.crayne.archivist.index.cache.IndexCache.rootDirectory;

public class ServerCache {

    @NotNull
    private final String name;

    @NotNull
    private final Path path;

    @NotNull
    private final Map<String, SaveCache> saveCacheMap;

    @NotNull
    private final Index index;

    @Nullable
    private BlobField blobField;

    public ServerCache(@NotNull final Index serverIndex) {
        this.name = serverIndex.title();
        this.path = serverIndex.path();
        this.index = serverIndex;
        this.saveCacheMap = new HashMap<>();
    }

    public void load() {
        blobField = new BlobField(name);
        index.children()
                .stream()
                .peek(Index::parse)
                .peek(index -> {
                    final SaveCache saveCache = new SaveCache(index);
                    saveCacheMap.put(index.title(), saveCache);
                    saveCache.parsePosition();
                })
                .map(Index::createSaveBlobs)
                .forEach(save -> save.forEach(blobField::merge));
    }

    public void copyRegionFiles() {
        if (blobField == null) return;

        final Path rootDirectory = rootDirectory();
        final var blobRegions = blobField.blobRegions();

        blobRegions.forEach(((blobLevel, regions) -> {
            final String fullIdentifier = blobLevel.fullIdentifier();
            final Path worldDirectory = rootDirectory.resolve(fullIdentifier);
            if (Files.exists(worldDirectory)) {
                ArchivistPlugin.log("Blob '" + fullIdentifier + "' already exists, skipping", Level.INFO);
                blobLevel.createWorld();
                return;
            }
            try {
                ArchivistPlugin.log("Creating blob '" + fullIdentifier + "'", Level.INFO);
                Files.createDirectories(worldDirectory);
                final Path regionDirectory = blobLevel.worldType().resolveRegionDirectory(worldDirectory);
                if (Files.exists(regionDirectory)) return;

                Files.createDirectories(regionDirectory);

                for (final Region region : regions) {
                    Files.copy(region.source(), regionDirectory.resolve(region.source().getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (final IOException e) {
                throw new IndexingException(e);
            }
            ArchivistPlugin.log("Successfully copied all region files to the new blob world", Level.INFO);
            ArchivistPlugin.log("Created blob level " + blobLevel.createWorld().getName(), Level.INFO);
        }));
    }

    @NotNull
    public Optional<SaveCache> resolveSaveCache(@NotNull final String save) {
        return Optional.ofNullable(saveCacheMap.get(save));
    }

    @NotNull
    public Optional<Path> resolveSavePath(@NotNull final String save, @NotNull final String variant) {
        return resolveSaveCache(save).map(saveCache -> saveCache.resolveVariant(variant));
    }

    @NotNull
    public Optional<World> resolveSaveLevel(@NotNull final String save, @NotNull final String variant) {
        return resolveSavePath(save, variant)
                .flatMap(path -> blobField().
                        flatMap(blobField -> blobField.findBlobLevel(path)))
                .map(BlobLevel::requireWorld);
    }

    @NotNull
    public Optional<Location> resolveSaveLocation(@NotNull final String save, @NotNull final String variant) {
        final Optional<SaveCache> saveCache = resolveSaveCache(save);
        final Optional<World> world = resolveSaveLevel(save, variant);
        if (saveCache.isEmpty() || world.isEmpty()) return Optional.empty();

        return saveCache.get().position().map(position -> position.toLocation(world.get()));
    }

    @NotNull
    public Optional<BlobField> blobField() {
        return Optional.ofNullable(blobField);
    }

    @NotNull
    public Path path() {
        return path;
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public Index index() {
        return index;
    }

    @NotNull
    public Map<String, SaveCache> saveCacheMap() {
        return Collections.unmodifiableMap(saveCacheMap);
    }

}
