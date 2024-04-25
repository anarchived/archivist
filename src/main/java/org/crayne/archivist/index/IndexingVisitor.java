package org.crayne.archivist.index;

import org.commonmark.node.*;
import org.crayne.archivist.index.section.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IndexingVisitor extends AbstractVisitor {

    @NotNull
    private final IndexFile indexFile;

    @Nullable
    private String currentHeader;

    @Nullable
    private Section currentSection;

    public IndexingVisitor(@NotNull final IndexFile indexFile) {
        this.indexFile = indexFile;
    }

    @NotNull
    public IndexFile indexFile() {
        return indexFile;
    }

    public void lineBreak() {
        if (currentSection != null) currentSection.append("\n");
    }

    public void append(@NotNull final String text) {
        if (currentSection == null) {
            currentHeader = text;
            return;
        }
        currentSection.append(text);
    }

    public void visit(@NotNull final Header header) {
        currentSection = null;
        visitChildren(header);

        assert currentHeader != null;
        currentSection = new Section(currentHeader);
        indexFile.headerSectionMap().put(currentHeader, currentSection);
    }

    public void visit(@NotNull final ListItem listItem) {
        append("- ");
        visitChildren(listItem);
        lineBreak();
    }

    public void visit(@NotNull final SoftLineBreak softLineBreak) {
        lineBreak();
    }

    public void visit(@NotNull final HardLineBreak hardLineBreak) {
        lineBreak();
    }

    public void visit(@NotNull final Text text) {
        append(text.getLiteral());
    }

}
