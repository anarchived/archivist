package org.crayne.archivist.index.archive;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.file.IndexFile;
import org.crayne.archivist.index.file.section.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class ArchiveIndex {

    @NotNull
    private final Path archivePath;

    @NotNull
    private final Map<Path, ArchiveIndex> subArchives;

    @Nullable
    private IndexFile indexFile;

    public ArchiveIndex(@NotNull final Path archivePath) {
        this.archivePath = archivePath;
        this.subArchives = new HashMap<>();
    }

    public void loadAll() {
        parse();
        index();
    }

    @NotNull
    public Map<Path, ArchiveIndex> subArchives() {
        return subArchives;
    }

    @NotNull
    public Optional<IndexFile> indexFile() {
        return Optional.ofNullable(indexFile);
    }

    @NotNull
    public Path archivePath() {
        return archivePath;
    }

    public void parse() {
        indexFile = new IndexFile(archivePath, requiredSections());
    }

    @NotNull
    public abstract Map<String, Section> requiredSections();

    public void index() {
        preprocess();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(archivePath(), Files::isDirectory)) {
            stream.forEach(p -> index(p).ifPresent(index -> subArchives.put(p, index)));
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
    }

    @NotNull
    public String requireTitle() {
        return indexFile()
                .orElseThrow(() -> new IndexingException("Index does not contain index file"))
                .title()
                .orElseThrow(() -> new IndexingException("Index file does not have a title"));
    }

    @NotNull
    public abstract Optional<ArchiveIndex> index(@NotNull final Path path);

    public void preprocess() {

    }

}
