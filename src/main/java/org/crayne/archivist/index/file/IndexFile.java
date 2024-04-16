package org.crayne.archivist.index.file;

import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.file.section.IndexSection;
import org.crayne.archivist.index.file.section.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

public class IndexFile {

    @NotNull
    private final Path indexFilePath, parentDirectory;

    @Nullable
    private String title;

    @NotNull
    private final Map<String, IndexSection<?>> sectionMap;

    @NotNull
    private final Map<String, Section> expectedSections;

    public IndexFile(@NotNull final Path parentDirectory, @NotNull final Map<String, Section> expectedSections) {
        this.parentDirectory = parentDirectory;
        this.indexFilePath = parentDirectory.resolve("index.md");
        this.sectionMap = new LinkedHashMap<>();
        this.expectedSections = new HashMap<>(expectedSections);

        parse();
    }

    public void parse() {
        try {
            parse(Files.readAllLines(indexFilePath));
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
    }

    @NotNull
    public Optional<String> title() {
        return Optional.ofNullable(title);
    }

    @NotNull
    public Map<String, IndexSection<?>> sectionMap() {
        return sectionMap;
    }

    public void parse(@NotNull final List<String> lines) {
        log("Parsing index file");
        if (lines.isEmpty()) throwError("Empty index.md");
        title = removeHeadingPrefix(lines.get(0));

        log("Parsed title: '" + title + "'");
        loadSections(lines);
        verifySections();
    }

    private void verifySections() {
        for (final String requiredSection : expectedSections.keySet()) {
            final Section section = expectedSections.get(requiredSection);
            if (!section.optional() && !sectionMap.containsKey(requiredSection))
                throwError("Missing " + section + " section '" + requiredSection + "'");
        }
    }

    private void loadSections(@NotNull final List<String> lines) {
        String lineInfo = "";
        String currentSection = null;
        StringBuilder currentSectionText = new StringBuilder();

        for (int i = 1; i < lines.size(); i++) {
            final String line = lines.get(i).trim();
            lineInfo = " (at line " + (i + 1) + ")";

            if (line.startsWith("#")) {
                addSection(currentSection, currentSectionText.toString(), lineInfo);
                currentSectionText = new StringBuilder();
                currentSection = removeHeadingPrefix(line);
                continue;
            }
            if (!line.isEmpty() && currentSection == null)
                continue;

            currentSectionText.append(line).append("\n");
        }
        addSection(currentSection, currentSectionText.toString(), lineInfo);
    }

    private void addSection(@Nullable final String currentSection, @NotNull final String currentSectionText,
                            @NotNull final String lineInfo) {
        if (currentSection == null) return;

        if (!expectedSections.containsKey(currentSection))
            throwError("Unexpected section '" + currentSection + "'" + lineInfo);

        final Section type = expectedSections.get(currentSection);
        final IndexSection<?> indexedSection;

        try {
            indexedSection = IndexSection.parseIndexSection(currentSection, currentSectionText, type.type());
        } catch (final IndexingException e) {
            throwError("Could not parse section '" + currentSection + "'" + lineInfo, e);
            return;
        }

        log("Parsed " + type + " section: '" + currentSection + "'");
        sectionMap.put(currentSection, indexedSection);
    }

    @NotNull
    private static String removeHeadingPrefix(@NotNull final String line) {
        final String trimmed = line.trim();
        if (!trimmed.startsWith("#")) return trimmed;

        int substr = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            if (trimmed.charAt(i) != '#') {
                substr = i;
                break;
            }
        }
        return trimmed.substring(substr).trim();
    }

    @NotNull
    private String createPrefix() {
        return "(in index file '" + indexFilePath + "'): ";
    }

    public void log(@NotNull final String s) {
        ArchivistPlugin.log(createPrefix() + s, Level.INFO);
    }

    public void throwError(@NotNull final String s) {
        throw new IndexingException(createPrefix() + s);
    }

    public void throwError(@NotNull final String s, @NotNull final Throwable t) {
        throw new IndexingException(createPrefix() + s, t);
    }

    @NotNull
    public Path indexFilePath() {
        return indexFilePath;
    }

    @NotNull
    public String toString() {
        return "IndexFile{" +
                "indexFilePath=" + indexFilePath +
                ", parentDirectory=" + parentDirectory +
                ", title='" + title + '\'' +
                ", sectionMap=" + sectionMap +
                ", expectedSections=" + expectedSections +
                '}';
    }
}
