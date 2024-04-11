package org.crayne.archivist.index.blob.region;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public enum Dimension {

    OVERWORLD("world"),
    NETHER("world_nether"),
    END("world_the_end");

    @NotNull
    private final String worldFolderName;

    Dimension(@NotNull final String worldFolderName) {
        this.worldFolderName = worldFolderName;
    }

    @NotNull
    public String worldFolderName() {
        return worldFolderName;
    }

    @NotNull
    public String toString() {
        return name().toLowerCase();
    }

    @NotNull
    public static Optional<Dimension> of(@NotNull final String name) {
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
    public Path resolveRegionFolder(@NotNull final Path path) {
        return switch (this) {
            case OVERWORLD -> path.resolve("region");
            case END -> path.resolve("DIM1").resolve("region");
            case NETHER -> path.resolve("DIM-1").resolve("region");
        };
    }

}
