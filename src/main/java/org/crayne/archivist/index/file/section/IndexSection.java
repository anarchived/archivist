package org.crayne.archivist.index.file.section;

import org.crayne.archivist.index.file.section.type.EnumerationSection;
import org.crayne.archivist.index.file.section.type.MapSection;
import org.crayne.archivist.index.file.section.type.TextSection;
import org.jetbrains.annotations.NotNull;

public abstract class IndexSection<T> {

    @NotNull
    private final String title, text;

    @NotNull
    private final T value;

    public IndexSection(@NotNull final String title, @NotNull final String text) {
        this.title = title;
        this.text = text;
        this.value = parse(text);
    }

    @NotNull
    public static IndexSection<?> parseIndexSection(@NotNull final String title, @NotNull final String text,
                                                    @NotNull final SectionType type) {
        return switch (type) {
            case TEXT -> new TextSection(title, text);
            case MAP -> new MapSection(title, text);
            case ENUMERATION -> new EnumerationSection(title, text);
        };
    }

    @NotNull
    public String text() {
        return text;
    }

    @NotNull
    public String title() {
        return title;
    }

    @NotNull
    public T value() {
        return value;
    }

    @NotNull
    public abstract T parse(@NotNull final String text);

}
