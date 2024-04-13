package org.crayne.archivist.index.cached;

import org.bukkit.Bukkit;
import org.crayne.archivist.index.blob.region.World;
import org.crayne.archivist.index.blob.save.Position;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class CachedSaveData {

    @NotNull
    private final Position position;

    @NotNull
    private final World world;

    @NotNull
    private final Map<String, String> variantWorldDefinitions;

    @NotNull
    private final String markdownContent;


    public CachedSaveData(@NotNull final Position position,
                          @NotNull final World world,
                          @NotNull final String markdownContent,
                          @NotNull final Map<String, String> variantWorldDefinitions) {
        this.position = position;
        this.world = world;
        this.markdownContent = markdownContent;
        this.variantWorldDefinitions = new HashMap<>(variantWorldDefinitions);
    }

    @NotNull
    public Map<String, String> variantWorldDefinitions() {
        return Collections.unmodifiableMap(variantWorldDefinitions);
    }

    @NotNull
    public String markdownContent() {
        return markdownContent;
    }

    @NotNull
    public Map<String, String> variantWorldDefinitionsSorted() {
        return MapUtil.sortMapByKey(variantWorldDefinitions);
    }

    @NotNull
    public List<Map.Entry<String, org.bukkit.World>> variantWorldsSorted() {
        return MapUtil.mapKeys(variantWorldDefinitionsSorted(), Bukkit::getWorld);
    }

    @NotNull
    public Position position() {
        return position;
    }

    @NotNull
    public World world() {
        return world;
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
