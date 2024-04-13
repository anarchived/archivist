package org.crayne.archivist.index.cached;

import org.crayne.archivist.index.blob.region.World;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

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

    @NotNull
    public Stream<Map.Entry<String, World>> streamWorldEntries() {
        return data()
                .variantWorldDefinitions()
                .values()
                .stream()
                .map(s -> Map.entry(s, data().world()));
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
