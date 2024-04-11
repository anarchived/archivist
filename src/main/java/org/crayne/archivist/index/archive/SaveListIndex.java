package org.crayne.archivist.index.archive;

import org.crayne.archivist.index.blob.Blob;
import org.crayne.archivist.index.blob.save.SaveIdentifier;
import org.crayne.archivist.index.file.section.Section;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class SaveListIndex extends ArchiveIndex {

    @NotNull
    private final Set<Blob> blobs;

    @NotNull
    private final Map<String, Set<SaveIdentifier>> indexedSaves;

    public SaveListIndex(@NotNull final Path archivePath) {
        super(archivePath);
        this.blobs = new HashSet<>();
        this.indexedSaves = new HashMap<>();
    }

    @NotNull
    private static final Map<String, Section> SECTIONS = Map.of(
            "Archived Saves", SectionType.ENUMERATION.createSection()
    );

    @NotNull
    public Map<String, Section> requiredSections() {
        return SECTIONS;
    }

    @NotNull
    public Set<Blob> blobs() {
        return blobs;
    }

    @NotNull
    public Map<String, Set<SaveIdentifier>> indexedSaves() {
        return indexedSaves;
    }

    @NotNull
    public Optional<ArchiveIndex> index(@NotNull final Path path) {
        final SaveIndex indexed = new SaveIndex(path);
        indexed.loadAll();

        final String saveName = indexed.requireTitle();
        indexed.variants().forEach(i -> {
            indexedSaves.putIfAbsent(saveName, new HashSet<>());
            indexedSaves.get(saveName).add(i);
        });
        blobs.addAll(indexed.blobs().values());

        return Optional.of(indexed);
    }
}