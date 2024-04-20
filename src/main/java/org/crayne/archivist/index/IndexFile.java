package org.crayne.archivist.index;

import net.kyori.adventure.text.Component;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.crayne.archivist.index.section.Section;
import org.crayne.archivist.text.markdown.MarkdownBookRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class IndexFile {

    @NotNull
    private final Path path;

    @NotNull
    private final Map<String, Section> headerSectionMap;

    @Nullable
    private Node cachedNode;

    @NotNull
    private Component @Nullable [] cachedMarkdownRender;

    public IndexFile(@NotNull final Path path) {
        this.path = path;
        this.headerSectionMap = new LinkedHashMap<>();
    }

    @NotNull
    public String read() {
        final String text;
        try {
            text = Files.readString(path);
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
        return text;
    }

    @NotNull
    public Optional<Section> section(@NotNull final String title) {
        return Optional.ofNullable(headerSectionMap.get(title));
    }

    @NotNull
    public Optional<Section> section(final int index) {
        final List<String> headers = headerSectionMap.keySet().stream().toList();
        if (index < 0 || index >= headers.size()) return Optional.empty();

        return section(headers.get(index));
    }

    @NotNull
    public Node document() {
        if (cachedNode != null)
            return cachedNode;

        final String text = read();
        final Parser markdownParser = Parser.builder().build();
        return cachedNode = markdownParser.parse(text);
    }

    @NotNull
    public Component @NotNull [] renderMarkdown() {
        if (cachedMarkdownRender != null)
            return cachedMarkdownRender;

        return cachedMarkdownRender = MarkdownBookRenderer.renderMarkdownToBookContent(document());
    }

    public void index() {
        final Node document = document();
        final IndexingVisitor markdownVisitor = new IndexingVisitor(this);
        document.accept(markdownVisitor);
    }

    @NotNull
    public String toString() {
        return "IndexFile{" +
                "path=" + path +
                ", headerSectionMap=" + headerSectionMap +
                '}';
    }

    @NotNull
    public Path path() {
        return path;
    }

    @NotNull
    public Map<String, Section> headerSectionMap() {
        return headerSectionMap;
    }

}
