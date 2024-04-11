package org.crayne.archivist.index.file.section;

import org.jetbrains.annotations.NotNull;

public class Section {

    @NotNull
    private final SectionType type;

    private final boolean optional;

    public Section(@NotNull final SectionType type, final boolean optional) {
        this.type = type;
        this.optional = optional;
    }

    public boolean optional() {
        return optional;
    }

    @NotNull
    public SectionType type() {
        return type;
    }

    public String toString() {
        return (optional ? "optional " : "") + type;
    }
}
