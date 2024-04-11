package org.crayne.archivist.index.file.section;

import org.jetbrains.annotations.NotNull;

public enum SectionType {
    TEXT,
    ENUMERATION,
    MAP;

    @NotNull
    public String toString() {
        return name().toLowerCase();
    }

    @NotNull
    public Section createSection(final boolean optional) {
        return new Section(this, optional);
    }

    @NotNull
    public Section createSection() {
        return createSection(false);
    }

}
