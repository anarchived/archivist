package org.crayne.archivist.index.blob;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.crayne.archivist.index.blob.region.WorldType;
import org.crayne.archivist.util.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BlobLevel(int blobIndex, @NotNull WorldType worldType, @NotNull String serverTitle) {

    @NotNull
    public World requireWorld() {
        return Objects.requireNonNull(Bukkit.getWorld(fullIdentifier()));
    }

    @NotNull
    public String fullIdentifier() {
        return serverTitle + "_blob" + blobIndex + "_" + worldType;
    }

    @NotNull
    public static World createWorld(@NotNull final String worldName, @NotNull final World.Environment environment) {
        final World world = Bukkit.createWorld(new WorldCreator(worldName)
                .generator(SpawnWorld.EMPTY_GENERATOR)
                .environment(environment)
        );
        assert world != null;
        world.setAutoSave(false);
        return world;
    }

    @NotNull
    public World createWorld() {
        return createWorld(fullIdentifier(), worldType.environment());
    }

}
