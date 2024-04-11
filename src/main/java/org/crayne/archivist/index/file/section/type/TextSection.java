package org.crayne.archivist.index.file.section.type;

import org.crayne.archivist.index.file.section.IndexSection;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;

public class TextSection extends IndexSection<String> {

    public TextSection(@NotNull final String title, @NotNull final String text) {
        super(title, text);
    }

    @NotNull
    public String parse(@NotNull final String text) {
        return text;
    }

    @NotNull
    public static SectionType type() {
        return SectionType.TEXT;
    }

}
