package org.crayne.archivist.index.blob;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.crayne.archivist.index.blob.region.World;
import org.crayne.archivist.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BlobLevel(int blobIndex, @NotNull World world, @NotNull String serverTitle) {

    @NotNull
    public org.bukkit.World requireWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(fullIdentifier()));
    }

    @NotNull
    public String fullIdentifier() {
        return serverTitle + "_blob" + blobIndex + "_" + world;
    }

    @NotNull
    public static org.bukkit.World createWorld(@NotNull final String worldName, @NotNull final org.bukkit.World.Environment environment) {
        final org.bukkit.World world = Bukkit.createWorld(new WorldCreator(worldName)
                .generator(SpawnWorld.EMPTY_GENERATOR)
                .environment(environment)
        );
        world.setAutoSave(false);
        return world;
    }

    @NotNull
    public org.bukkit.World createWorld() {
        return createWorld(fullIdentifier(), world.environment());
    }

}
