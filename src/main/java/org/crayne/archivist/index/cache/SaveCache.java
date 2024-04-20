package org.crayne.archivist.index.cache;

import org.bukkit.World;
import org.crayne.archivist.index.Index;
import org.crayne.archivist.index.blob.BlobField;
import org.crayne.archivist.index.blob.BlobLevel;
import org.crayne.archivist.util.MapUtil;
import org.crayne.archivist.util.world.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SaveCache {

    @NotNull
    private final String name;

    @NotNull
    private final Path path;

    @NotNull
    private final Index index;

    @Nullable
    private Position position;

    @NotNull
    private final Map<String, World> variantWorlds;

    public SaveCache(@NotNull final Index index) {
        this.name = index.title();
        this.path = index.path();
        this.index = index;
        this.variantWorlds = new HashMap<>();
    }

    public void loadVariants(@NotNull final BlobField blobField) {
        index.children().forEach(variant -> {
            final Optional<BlobLevel> level = blobField.findBlobLevel(variant.path());
            if (level.isEmpty()) return;

            final World world = level.get().requireWorld();
            variantWorlds.put(variant.title(), world);
        });
    }

    @NotNull
    public Path resolveVariant(@NotNull final String variantName) {
        return path.resolve(variantName);
    }

    public void parsePosition() {
        index.indexPosition().ifPresent(position -> this.position = position);
    }

    @NotNull
    public Map<String, World> variants() {
        return MapUtil.sortMapByKey(variantWorlds);
    }

    @NotNull
    public String name() {
        return name;
    }

    @NotNull
    public Path path() {
        return path;
    }

    @NotNull
    public Index index() {
        return index;
    }

    @NotNull
    public Optional<Position> position() {
        return Optional.ofNullable(position);
    }

}
