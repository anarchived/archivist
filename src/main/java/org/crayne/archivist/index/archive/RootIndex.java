package org.crayne.archivist.index.archive;

import org.crayne.archivist.index.file.section.Section;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class RootIndex extends ArchiveIndex {

    @Nullable
    private ServerListIndex serverListIndex;

    public RootIndex(@NotNull final Path archivePath) {
        super(archivePath);
    }

    @NotNull
    private static final Map<String, Section> SECTIONS = Map.of(
            "Description", SectionType.TEXT.createSection(),
            "Credits", SectionType.ENUMERATION.createSection(true)
    );

    @NotNull
    public Map<String, Section> requiredSections() {
        return SECTIONS;
    }

    @NotNull
    public Optional<ServerListIndex> serverListIndex() {
        return Optional.ofNullable(serverListIndex);
    }

    @NotNull
    public Optional<ArchiveIndex> index(@NotNull final Path path) {
        if (path.getFileName().toString().equals("servers")) {
            serverListIndex = new ServerListIndex(path);
            serverListIndex.loadAll();
            return Optional.of(serverListIndex);
        }
        return Optional.empty();
    }

}
