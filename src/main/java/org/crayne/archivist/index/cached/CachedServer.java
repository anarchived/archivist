package org.crayne.archivist.index.cached;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class CachedServer {

    @NotNull
    private final String name;

    @NotNull
    private final Map<String, CachedSave> saves;

    public CachedServer(@NotNull final String name, @NotNull final Map<String, CachedSave> saves) {
        this.name = name;
        this.saves = saves;
    }

    @NotNull
    public Map<String, CachedSave> saves() {
        return Collections.unmodifiableMap(saves);
    }

    @NotNull
    public String name() {
        return name;
    }

    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CachedServer) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.saves, that.saves);
    }

    public int hashCode() {
        return Objects.hash(name, saves);
    }

    @NotNull
    public String toString() {
        return "CachedServer[" +
                "name=" + name + ", " +
                "saves=" + saves + ']';
    }

}
