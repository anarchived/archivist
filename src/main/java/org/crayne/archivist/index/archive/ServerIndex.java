package org.crayne.archivist.index.archive;

import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.file.section.Section;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

public class ServerIndex extends ArchiveIndex {

    @Nullable
    private BlobField blobField;

    @NotNull
    private final Map<String, Set<SaveIdentifier>> indexedSaves;

    public ServerIndex(@NotNull final Path archivePath) {
        super(archivePath);
        this.indexedSaves = new HashMap<>();
    }

    @NotNull
    private static final Map<String, Section> SECTIONS = Map.of(
            "Description", SectionType.TEXT.createSection()
    );

    @NotNull
    public Map<String, Section> requiredSections() {
        return SECTIONS;
    }

    @NotNull
    public BlobField blobField() {
        return Objects.requireNonNull(blobField);
    }

    public void parse() {
        super.parse();
        this.blobField = new BlobField(requireTitle());
    }

    @NotNull
    public Map<String, Set<SaveIdentifier>> indexedSaves() {
        return indexedSaves;
    }

    @NotNull
    public Optional<ArchiveIndex> index(@NotNull final Path path) {
        final SaveListIndex indexed = new SaveListIndex(path);
        indexed.loadAll();
        indexed.blobs().forEach(blobField()::merge);
        indexedSaves.putAll(indexed.indexedSaves());

        return Optional.of(indexed);
    }

}
