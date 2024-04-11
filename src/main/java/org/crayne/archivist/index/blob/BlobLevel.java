package org.crayne.archivist.index.blob;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.crayne.archivist.index.blob.region.Dimension;
import org.crayne.archivist.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BlobLevel(int blobIndex, @NotNull Dimension dimension, @NotNull String serverTitle) {

    @NotNull
    public World requireWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(fullIdentifier()));
    }

    @NotNull
    public String fullIdentifier() {
        return serverTitle + "_blob" + blobIndex + "_" + dimension;
    }

    @NotNull
    public static World createWorld(@NotNull final String worldName, @NotNull final World.Environment environment) {
        return Bukkit.createWorld(new WorldCreator(worldName)
                .generator(SpawnWorld.EMPTY_GENERATOR)
                .environment(environment)
        );
    }

    @NotNull
    public World createWorld() {
        return createWorld(fullIdentifier(), dimension.environment());
    }

}
