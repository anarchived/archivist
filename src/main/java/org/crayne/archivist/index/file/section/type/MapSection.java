package org.crayne.archivist.index.file.section.type;

import org.apache.commons.lang3.StringUtils;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.file.section.IndexSection;
import org.crayne.archivist.index.file.section.SectionType;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSection extends IndexSection<Map<String, String>> {

    public MapSection(@NotNull final String title, @NotNull final String text) {
        super(title, text);
    }

    @NotNull
    public Map<String, String> parse(@NotNull final String text) {
        final Map<String, String> result = new LinkedHashMap<>();
        for (final String line : text.split("\n")) {
            final String trimmed = line.trim();
            if (!trimmed.startsWith("-"))
                throw new IndexingException("Invalid map; Expected '-' as first character of every line");

            if (!trimmed.contains(":"))
                throw new IndexingException("Invalid map; Expected ':' to separate key and value");

            final String sanitized = trimmed.substring(1).trim();
            final String key = StringUtils.substringBefore(sanitized, ":").trim();
            final String value = StringUtils.substringAfter(sanitized, ":").trim();
            result.put(key, value);
        }
        return result;
    }

    @NotNull
    public static SectionType type() {
        return SectionType.ENUMERATION;
    }

}
