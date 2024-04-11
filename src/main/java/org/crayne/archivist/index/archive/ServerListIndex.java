package org.crayne.archivist.index.archive;

import org.crayne.archivist.index.file.section.Section;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ServerListIndex extends ArchiveIndex {

    @NotNull
    private final Set<ServerIndex> serverIndices;

    public ServerListIndex(@NotNull final Path archivePath) {
        super(archivePath);
        this.serverIndices = new HashSet<>();
    }

    @NotNull
    private static final Map<String, Section> SECTIONS = Map.of(
            "Servers", SectionType.ENUMERATION.createSection()
    );

    @NotNull
    public Map<String, Section> requiredSections() {
        return SECTIONS;
    }

    @NotNull
    public Set<ServerIndex> serverIndices() {
        return serverIndices;
    }

    @NotNull
    public Optional<ArchiveIndex> index(@NotNull final Path path) {
        final ServerIndex indexed = new ServerIndex(path);
        indexed.loadAll();
        serverIndices.add(indexed);

        return Optional.of(indexed);
    }

}
