package org.crayne.archivist.index;

import org.crayne.archivist.index.blob.Blob;
import org.crayne.archivist.index.blob.region.Region;
import org.crayne.archivist.index.blob.region.WorldType;
import org.crayne.archivist.index.section.Section;
import org.crayne.archivist.index.tags.MultiTag;
import org.crayne.archivist.util.world.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Index {

    @Nullable
    private final IndexFile indexFile;

    @NotNull
    private final List<Index> children;

    @NotNull
    private final List<Path> files;

    @NotNull
    private final Path path;

    public Index(@NotNull final Path filePath) {
        final Path indexFilePath = filePath.resolve("index.md");
        this.path = filePath;

        this.indexFile = Files.exists(indexFilePath) ? new IndexFile(indexFilePath) : null;
        this.children = indexChildren();
        this.files = indexFiles();
    }

    @NotNull
    public Optional<IndexFile> indexFile() {
        return Optional.ofNullable(indexFile);
    }

    @NotNull
    public List<Index> children() {
        return children;
    }

    @NotNull
    public Path path() {
        return path;
    }

    @NotNull
    public List<Path> files() {
        return files;
    }

    public void parse() {
        indexFile().ifPresent(IndexFile::index);
    }

    @NotNull
    public List<List<Region>> regionFiles(@NotNull final WorldType worldType) {
        return children.stream()
                .map(child -> child.files.stream()
                        .map(path -> Region.parse(path, worldType))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList())
                .toList();
    }

    @NotNull
    public List<List<Region>> regionFiles() {
        parse();
        final Optional<WorldType> worldType = indexWorldType();

        return worldType.map(this::regionFiles)
                .orElse(Collections.emptyList());
    }

    @NotNull
    public List<Blob> createSaveBlobs() {
        final Optional<String> title = indexTitle();
        final Optional<WorldType> worldType = indexWorldType();

        if (title.isEmpty() || worldType.isEmpty())
            return Collections.emptyList();

        return regionFiles()
                .stream()
                .map(regions -> new Blob(regions, worldType.get()))
                .toList();
    }

    @NotNull
    public String title() {
        return indexTitle().orElse(path.getFileName().toString());
    }

    @NotNull
    public List<MultiTag> indexTags() {
        return indexFile()
                .flatMap(i -> i.section("Tags"))
                .map(Section::enumeration)
                .map(enumeration -> enumeration.stream()
                        .map(MultiTag::parse)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @NotNull
    public Optional<WorldType> indexWorldType() {
        return indexFile()
                .flatMap(i -> i.section("Location"))
                .flatMap(section -> section.mapped("World"))
                .flatMap(WorldType::of);
    }

    @NotNull
    public Optional<Position> indexPosition() {
        return indexFile()
                .flatMap(i -> i.section("Location"))
                .flatMap(section -> section.mapped("Position"))
                .flatMap(Position::of);
    }

    @NotNull
    public Optional<String> indexTitle() {
        return indexFile()
                .flatMap(i -> i.section(0))
                .map(Section::title);
    }

    @NotNull
    private List<Index> indexChildren() {
        return indexFiles(Index::includeIndex)
                .stream()
                .map(Index::new)
                .toList();
    }

    @NotNull
    private List<Path> indexFiles() {
        return indexFiles(Index::includeFile);
    }

    @NotNull
    public List<Path> mapDataFiles() {
        return indexFiles(Index::mapDataFile);
    }

    @NotNull
    private List<Path> indexFiles(@NotNull final DirectoryStream.Filter<? super Path> filter) {
        final List<Path> result = new ArrayList<>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path, filter)) {
            stream.forEach(result::add);
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
        return result;
    }

    private static boolean includeFile(@NotNull final Path path) {
        return includePath(path) && !Files.isDirectory(path);
    }

    private static boolean includeIndex(@NotNull final Path path) {
        return includePath(path) && Files.isDirectory(path);
    }

    private static boolean includePath(@NotNull final Path path) {
        return !path.getFileName().toString().startsWith(".");
    }

    private static boolean mapDataFile(@NotNull final Path path) {
        final String fileName = path.getFileName().toString();
        return fileName.startsWith("map_") && fileName.endsWith(".dat") && !Files.isDirectory(path);
    }

    @NotNull
    public String toString() {
        final String childrenToString = indexChildren()
                .stream()
                .map(Index::toString)
                .map(s -> "- " + s)
                .collect(Collectors.joining())
                .indent(3);

        return path.getFileName() + "\n" + childrenToString;
    }

}
