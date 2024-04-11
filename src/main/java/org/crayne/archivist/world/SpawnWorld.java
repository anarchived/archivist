package org.crayne.archivist.world;

import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

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
    public World spawnWorld() {
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
        @NotNull
        public ChunkData generateChunkData(@NotNull final World world, @NotNull final Random random,
                                           final int chunkX, final int chunkZ, @NotNull final BiomeGrid biome) {
            final ChunkData data = createChunkData(world);

            for (int y = 0; y < data.getMaxHeight(); y++) {
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        if (x == 0 && y == 63 && z == 0 && chunkX == 0 && chunkZ == 0) {
                            data.setBlock(x, y, z, Material.STONE);
                            continue;
                        }
                        data.setBlock(x, y, z, Material.AIR);
                        biome.setBiome(x, z, Biome.PLAINS);
                    }
                }
            }
            return data;
        }
    };

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    public static World registerSpawnWorld() {
        return Bukkit.createWorld(new WorldCreator("spawn").generator(EMPTY_GENERATOR));
    }

}
