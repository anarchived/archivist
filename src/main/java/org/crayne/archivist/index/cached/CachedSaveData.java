package org.crayne.archivist.index.cached;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.crayne.archivist.index.blob.save.Position;
import org.crayne.archivist.index.blob.region.Dimension;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class CachedSaveData {

    @NotNull
    private final Position position;

    @NotNull
    private final Dimension dimension;

    @NotNull
    private final Map<String, String> variantWorldDefinitions;


    public CachedSaveData(@NotNull final Position position,
                          @NotNull final Dimension dimension,
                          @NotNull final Map<String, String> variantWorldDefinitions) {
        this.position = position;
        this.dimension = dimension;
        this.variantWorldDefinitions = new HashMap<>(variantWorldDefinitions);
    }

    @NotNull
    public Map<String, String> variantWorldDefinitions() {
        return Collections.unmodifiableMap(variantWorldDefinitions);
    }

    @NotNull
    public Map<String, String> variantWorldDefinitionsSorted() {
        return variantWorldDefinitions.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new)
                );
    }

    @NotNull
    public List<Map.Entry<String, World>> variantWorldsSorted() {
        return variantWorldDefinitionsSorted().entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), Bukkit.getWorld(e.getValue())))
                .toList();
    }

    @NotNull
    public Position position() {
        return position;
    }

    @NotNull
    public Dimension dimension() {
        return dimension;
    }

    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CachedSaveData) obj;
        return Objects.equals(this.position, that.position) &&
                Objects.equals(this.variantWorldDefinitions, that.variantWorldDefinitions);
    }

    public int hashCode() {
        return Objects.hash(position, variantWorldDefinitions);
    }

    @NotNull
    public String toString() {
        return "CachedSaveData[" +
                "position=" + position + ", " +
                "variantWorldDefinitions=" + variantWorldDefinitions + ']';
    }

}
