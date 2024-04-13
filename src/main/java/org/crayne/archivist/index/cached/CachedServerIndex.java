package org.crayne.archivist.index.cached;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.archive.RootIndex;
import org.crayne.archivist.index.archive.SaveIndex;
import org.crayne.archivist.index.archive.ServerIndex;
import org.crayne.archivist.index.archive.ServerListIndex;
import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.BlobLevel;
import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.blob.save.Position;
import org.crayne.archivist.index.blob.region.World;
import org.crayne.archivist.index.blob.region.Region;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CachedServerIndex {

    @NotNull
    private final Map<String, CachedServer> cachedServers;

    @NotNull
    private final Map<String, World> blobs;

    public CachedServerIndex(@NotNull final Map<String, CachedServer> cachedServers) {
        this.cachedServers = new HashMap<>(cachedServers);
        this.blobs = new HashMap<>(collectBlobs());
    }

    @NotNull
    public Map<String, CachedServer> cachedServers() {
        return Collections.unmodifiableMap(cachedServers);
    }

    @NotNull
    public Stream<CachedServer> cachedServerStream() {
        return cachedServers.values().stream();
    }

    @NotNull
    public Map<String, World> collectBlobs() {
        return cachedServerStream()
                .flatMap(CachedServer::streamWorldEntries)
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public Location findSaveLocation(@NotNull final String server, @NotNull final String save,
                                     @NotNull final String variant) {
        final CachedServer foundServer = cachedServers.get(server);
        if (foundServer == null)
            throw new IndexingException("Could not find server '" + server + "'");

        final CachedSave foundSave = foundServer.saves().get(save);
        if (foundSave == null)
            throw new IndexingException("Could not find save '" + save + "'");

        final Map<String, String> variants = foundSave.data().variantWorldDefinitions();
        final String foundWorld = variants.get(variant);
        if (foundWorld == null)
            throw new IndexingException("Could not find save variant '" + variant + "'");

        final org.bukkit.World world = Bukkit.getWorld(foundWorld);
        if (world == null)
            throw new IndexingException("Internal error; This world has not been loaded yet");

        final Position position = foundSave.data().position();
        return position.toLocation(world);
    }

    @NotNull
    public static Path cachedServerIndexPath() {
        return rootDirectory().resolve("server-index.json");
    }

    @NotNull
    public static Optional<CachedServerIndex> loadServerIndex() {
        ArchivistPlugin.log("Loading server index...", Level.INFO);
        if (Files.exists(cachedServerIndexPath())) {
            ArchivistPlugin.log("Loading server index from cached server-index.json", Level.INFO);
            final CachedServerIndex cached = loadCachedServerIndex();
            cached.blobs.forEach((worldName, world) -> BlobLevel.createWorld(worldName, world.environment()));
            return Optional.of(cached);
        }
        ArchivistPlugin.log("Server index file not found; Creating a new index from archive...", Level.INFO);

        final RootIndex rootIndex = loadRootArchive();
        final Optional<Set<ServerIndex>> serverIndices = rootIndex
                .serverListIndex()
                .map(ServerListIndex::serverIndices);

        ArchivistPlugin.log("Successfully indexed archive with "
                + serverIndices.map(Set::size).orElse(0)
                + " servers", Level.INFO);

        return serverIndices.map(CachedServerIndex::loadServerIndex);
    }

    @NotNull
    public static CachedServerIndex loadServerIndex(@NotNull final Set<ServerIndex> serverIndices) {
        if (Files.exists(cachedServerIndexPath())) return loadCachedServerIndex();

        return createIndex(serverIndices);
    }

    @NotNull
    public static CachedServerIndex loadCachedServerIndex() {
        final Gson gson = new Gson();
        try {
            return gson.fromJson(Files.readString(
                            cachedServerIndexPath()),
                    CachedServerIndex.class);
        } catch (final IOException e) {
            throw new IndexingException("Could not load cached server-index.json", e);
        }
    }

    @NotNull
    public static CachedServerIndex createIndex(@NotNull final Set<ServerIndex> serverIndices) {
        final Map<String, CachedServer> serverCache = new HashMap<>();

        serverIndices.forEach(serverIndex -> {
            final String serverName = serverIndex.requireTitle();
            final Map<String, Set<SaveIdentifier>> indexedSaves = serverIndex.indexedSaves();
            final BlobField blobField = serverIndex.blobField();
            final Map<BlobLevel, Collection<Region>> blobRegions = blobField.blobRegions();
            final String markdownContent;
            try {
                markdownContent = Files.readString(serverIndex.indexFile().orElseThrow().indexFilePath());
            } catch (final IOException e) {
                throw new IndexingException(e);
            }

            ArchivistPlugin.log("Found server index " + serverName
                    + " with " + indexedSaves.size()
                    + " saves and " + blobRegions.size() + " blobs", Level.INFO);

            copyRemainingRegionFiles(blobRegions);
            final CachedServer server = createCachedServer(serverName, markdownContent, indexedSaves, blobField);
            serverCache.put(serverName, server);
        });
        return cacheIndex(new CachedServerIndex(serverCache));
    }

    @NotNull
    public static CachedServerIndex cacheIndex(@NotNull final CachedServerIndex uncached) {
        final Gson gson = new Gson();
        try {
            Files.writeString(cachedServerIndexPath(), gson.toJson(uncached));
        } catch (final IOException e) {
            throw new IndexingException("Could not save cached server index to server-index.json", e);
        }
        return uncached;
    }

    @NotNull
    public static Path rootDirectory() {
        return Path.of(ArchivistPlugin.instance().getDataFolder().toURI()).getParent().getParent();
    }

    @NotNull
    public static RootIndex loadRootArchive() {
        final Path rootDirectory = rootDirectory();
        final RootIndex rootArchiveIndex = new RootIndex(rootDirectory.resolve("archive"));
        rootArchiveIndex.loadAll();
        return rootArchiveIndex;
    }

    public static void copyRemainingRegionFiles(@NotNull final Map<BlobLevel, Collection<Region>> blobRegions) {
        final Path rootDirectory = rootDirectory();

        blobRegions.forEach(((blobLevel, regions) -> {
            final org.bukkit.World world = blobLevel.createWorld();

            ArchivistPlugin.log("Created blob level " + world.getName(), Level.INFO);

            final String fullIdentifier = blobLevel.fullIdentifier();
            final Path worldFolder = rootDirectory.resolve(fullIdentifier);

            if (!Files.isDirectory(worldFolder))
                throw new IndexingException("The blob path is not a valid directory; Cannot copy region files");

            final Path regionFolder = blobLevel.world().resolveRegionFolder(worldFolder);
            try {
                FileUtils.deleteDirectory(regionFolder.toFile());
                Files.createDirectory(regionFolder);

                for (final Region region : regions) {
                    Files.copy(region.source(), regionFolder.resolve(region.source().getFileName()), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (final IOException e) {
                throw new IndexingException(e);
            }
            ArchivistPlugin.log("Successfully copied all region files to the new blob", Level.INFO);
        }));
    }

    @NotNull
    public static CachedServer createCachedServer(@NotNull final String serverName,
                                                  @NotNull final String markdownContent,
                                                  @NotNull final Map<String, Set<SaveIdentifier>> indexedSaves,
                                                  @NotNull final BlobField blobField) {
        final Map<String, CachedSave> saveCache = new HashMap<>();
        for (final String saveName : indexedSaves.keySet()) {
            ArchivistPlugin.log("Caching save " + saveName, Level.INFO);

            final Map<String, String> variantCache = new HashMap<>();
            final Set<SaveIdentifier> variants = indexedSaves.get(saveName);

            SaveIndex saveIndex = null;

            for (final SaveIdentifier variant : variants) {
                final BlobLevel blobLevel = blobField
                        .findBlobLevel(variant)
                        .orElseThrow(() -> new IndexingException("Could not find blob level of previously indexed save"));

                final String fullIdentifier = blobLevel.fullIdentifier();
                ArchivistPlugin.log("Caching save variant " + variant + " in " + fullIdentifier, Level.INFO);
                variantCache.put(variant.saveVariant(), fullIdentifier);
                saveIndex = variant.save();
            }
            if (saveIndex == null)
                throw new IndexingException("Cannot load indexed save with no variants");

            final String saveMarkdownContent;
            try {
                saveMarkdownContent = Files.readString(saveIndex.indexFile().orElseThrow().indexFilePath());
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
            final CachedSaveData data = new CachedSaveData(saveIndex.position(), saveIndex.world(), saveMarkdownContent, variantCache);
            final CachedSave save = new CachedSave(saveName, data);
            saveCache.put(saveName, save);
        }
        return new CachedServer(serverName, saveCache, markdownContent);
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CachedServerIndex) obj;
        return Objects.equals(this.cachedServers, that.cachedServers);
    }

    public int hashCode() {
        return Objects.hash(cachedServers);
    }

    @NotNull
    public String toString() {
        return "CachedServerIndex[" +
                "cachedServers=" + cachedServers + ']';
    }


}
