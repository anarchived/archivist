package org.crayne.archivist.util.world;

import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class SpawnWorld implements Listener {

    @NotNull
    private final World spawnWorld;

    @NotNull
    private final Location spawnLocation;

    public SpawnWorld() {
        spawnWorld = registerSpawnWorld();
        spawnLocation = new Location(spawnWorld, 0.5, 64, 0.5);
    }

    @NotNull
    public World spawnDimension() {
        return spawnWorld;
    }

    @NotNull
    public Location spawnLocation() {
        return spawnLocation;
    }

    @EventHandler
    public void joinEvent(@NotNull final PlayerJoinEvent ev) {
        ev.getPlayer().teleport(spawnLocation);
    }

    @EventHandler
    public void respawnEvent(@NotNull final PlayerRespawnEvent ev) {
        ev.setRespawnLocation(spawnLocation);
    }

    @NotNull
    public static final ChunkGenerator EMPTY_GENERATOR = new ChunkGenerator() {

        public void generateSurface(@NotNull final WorldInfo worldInfo,
                                    @NotNull final Random random,
                                    final int chunkX, final int chunkZ,
                                    @NotNull final ChunkData data) {
            for (int y = 0; y < data.getMaxHeight(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (x == 0 && y == 63 && z == 0 && chunkX == 0 && chunkZ == 0) {
                            data.setBlock(x, y, z, Material.STONE);
                            continue;
                        }
                        data.setBlock(x, y, z, Material.AIR);
                    }
                }
            }
        }
    };

    @NotNull
    public static World registerSpawnWorld() {
        return Objects.requireNonNull(Bukkit.createWorld(new WorldCreator("spawn").generator(EMPTY_GENERATOR)));
    }

}
