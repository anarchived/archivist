package org.crayne.archivist.index.blob.region;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public enum WorldType {

    OVERWORLD("worldType"),
    NETHER("world_nether"),
    END("world_the_end");

    @NotNull
    private final String worldDirectoryName;

    WorldType(@NotNull final String worldDirectoryName) {
        this.worldDirectoryName = worldDirectoryName;
    }

    @NotNull
    public String worldDirectoryName() {
        return worldDirectoryName;
    }

    @NotNull
    public String toString() {
        return name().toLowerCase();
    }

    @NotNull
    public static Optional<WorldType> of(@NotNull final String name) {
        return Arrays.stream(values())
                .filter(d -> d.name().equalsIgnoreCase(name))
                .findAny();
    }

    @NotNull
    public World.Environment environment() {
        return switch (this) {
            case OVERWORLD -> World.Environment.NORMAL;
            case END -> World.Environment.THE_END;
            case NETHER -> World.Environment.NETHER;
        };
    }

    @NotNull
    public Path resolveRegionDirectory(@NotNull final Path path) {
        return switch (this) {
            case OVERWORLD -> path.resolve("region");
            case END -> path.resolve("DIM1").resolve("region");
            case NETHER -> path.resolve("DIM-1").resolve("region");
        };
    }

}
