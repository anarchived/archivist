package org.crayne.archivist.index.archive;

import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.blob.Blob;
import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.blob.save.Position;
import org.crayne.archivist.index.blob.region.Dimension;
import org.crayne.archivist.index.blob.region.Region;
import org.crayne.archivist.index.file.section.IndexSection;
import org.crayne.archivist.index.file.section.Section;
import org.crayne.archivist.index.file.section.SectionType;
import org.crayne.archivist.index.file.section.type.MapSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class SaveIndex extends ArchiveIndex {

    @Nullable
    private Dimension dimension;

    @Nullable
    private Position position;

    @NotNull
    private final Map<UUID, Blob> blobs;

    @NotNull
    private final Set<SaveIdentifier> variants;

    public SaveIndex(@NotNull final Path archivePath) {
        super(archivePath);
        blobs = new HashMap<>();
        variants = new HashSet<>();
    }

    public void preprocess() {
        final Map<String, IndexSection<?>> indexed = indexFile()
                .orElseThrow(() -> new IndexingException("Failed to index save; Index file was not found"))
                .sectionMap();

        final IndexSection<?> locationSection = indexed.get("Location");
        if (!(locationSection instanceof final MapSection location))
            throw new IndexingException("Failed to index save; Location section is not of type map");

        final String dimensionName = location.value().get("Dimension");
        if (dimensionName == null)
            throw new IndexingException("Failed to index save; Location does not include 'Dimension' key");

        final String positionString = location.value().get("Position");
        if (positionString == null)
            throw new IndexingException("Failed to index save; Location does not include 'Position' key");

        this.dimension = Dimension.of(dimensionName)
                .orElseThrow(() -> new IndexingException("Failed to index save; Dimension is not valid (" + dimensionName + ")"));

        this.position = Position.of(positionString)
                .orElseThrow(() -> new IndexingException("Failed to index save; Position is not valid (" + positionString + ")"));
    }

    @NotNull
    private static final Map<String, Section> SECTIONS = Map.of(
            "Members", SectionType.ENUMERATION.createSection(),
            "Status", SectionType.TEXT.createSection(),
            "Location", SectionType.MAP.createSection(),
            "Archived Dates", SectionType.ENUMERATION.createSection(),
            "Sources", SectionType.ENUMERATION.createSection(),
            "Description", SectionType.TEXT.createSection(),
            "Additional Information", SectionType.TEXT.createSection(true)
    );

    @NotNull
    private static final Set<String> IGNORED_SUBDIRECTORIES = Set.of(
            "pictures"
    );

    @NotNull
    public Map<String, Section> requiredSections() {
        return SECTIONS;
    }

    private static boolean isRegionFile(@NotNull final Path f) {
        return Files.isReadable(f) && f.getFileName().toString().endsWith(".mca");
    }

    @NotNull
    public Region parseRegion(@NotNull final String fileName, @NotNull final Path source) {
        verifyDimension();

        // region filenames follow the format r.X.Z.mca, so we only care about indices 1 and 2 when splitting by '.'
        final String[] split = fileName.split("\\.");
        final String regionXStr = split[1];
        final String regionZStr = split[2];

        final int regionX, regionZ;
        try {
            regionX = Integer.parseInt(regionXStr);
            regionZ = Integer.parseInt(regionZStr);
        } catch (final NumberFormatException e) {
            throw new IndexingException(e);
        }
        assert dimension != null;
        return new Region(regionX, regionZ, dimension, source);
    }

    @NotNull
    public Blob createBlob(@NotNull final Path path) {
        ArchivistPlugin.log("Creating blob for " + path.getFileName(), Level.INFO);
        verifyDimension();
        final Path regionDirectory = path.resolve("region");

        final Set<Region> regions = new HashSet<>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(regionDirectory, SaveIndex::isRegionFile)) {
            stream.forEach(p -> regions.add(parseRegion(p.getFileName().toString(), p)));
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
        assert dimension != null;
        return new Blob(regions, dimension);
    }

    private void verifyDimension() {
        if (dimension == null)
            throw new IndexingException("Could not index save; Dimension was not loaded correctly");
    }

    @NotNull
    public Map<UUID, Blob> blobs() {
        return blobs;
    }

    @NotNull
    public Set<SaveIdentifier> variants() {
        return variants;
    }

    @NotNull
    public Optional<ArchiveIndex> index(@NotNull final Path path) {
        final String saveName = requireTitle();
        final String variant = path.getFileName().toString();

        if (IGNORED_SUBDIRECTORIES.contains(variant))
            return Optional.empty();

        final Blob blob = createBlob(path);
        final UUID uuid = blob.findAnyRegionUUID().orElse(null);

        if (uuid == null)
            throw new IndexingException("Cannot add empty save to archive (" + path + ")");

        if (position == null)
            throw new IndexingException("Cannot add save with unknown position to archive (" + path + ")");

        blobs.put(uuid, blob);
        variants.add(new SaveIdentifier(saveName, variant, uuid, position));
        return Optional.empty();
    }
}