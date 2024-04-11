package org.crayne.archivist.index.cached;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CachedSave {

    @NotNull
    private final String name;

    @NotNull
    private final CachedSaveData data;

    public CachedSave(@NotNull final String name, @NotNull final CachedSaveData data) {
        this.name = name;
        this.data = data;
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public CachedSaveData data() {
        return data;
    }

    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CachedSave) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.data, that.data);
    }

    public int hashCode() {
        return Objects.hash(name, data);
    }

    @NotNull
    public String toString() {
        return "CachedSave[" +
                "name=" + name + ", " +
                "data=" + data + ']';
    }
}
