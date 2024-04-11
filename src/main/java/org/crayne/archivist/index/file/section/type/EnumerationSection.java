package org.crayne.archivist.index.file.section.type;

import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.file.section.IndexSection;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnumerationSection extends IndexSection<List<String>> {

    public EnumerationSection(@NotNull final String title, @NotNull final String text) {
        super(title, text);
    }

    @NotNull
    public List<String> parse(@NotNull final String text) {
        final List<String> result = new ArrayList<>();
        for (final String line : text.split("\n")) {
            final String trimmed = line.trim();
            if (!trimmed.startsWith("-"))
                throw new IndexingException("Invalid enumeration; Expected '-' as first character of every line");

            result.add(trimmed.substring(1).trim());
        }
        return result;
    }

    @NotNull
    public static SectionType type() {
        return SectionType.ENUMERATION;
    }

}
