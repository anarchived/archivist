package org.crayne.archivist;

import mc.obliviate.inventory.InventoryAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crayne.archivist.command.*;
import org.crayne.archivist.consolefilter.LogSpamFilter;
import org.crayne.archivist.index.cache.IndexCache;
import org.crayne.archivist.index.maps.MapColorTable;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.listeners.MapLoadListener;
import org.crayne.archivist.listeners.WorldListener;
import org.crayne.archivist.util.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ArchivistPlugin extends JavaPlugin {

    private SpawnWorld spawnWorld;
    private IndexCache indexCache;

    @NotNull
    private final MapColorTable mapColorTable = new MapColorTable();

    private static ArchivistPlugin INSTANCE;

    public void onEnable() {
        INSTANCE = this;
        registerListeners(
                spawnWorld = new SpawnWorld(),
                new WorldListener(),
                new ArchivistInventory(),
                new MapLoadListener()
        );
        indexCache = new IndexCache();
        indexCache.load();
        indexCache.copyRegionFiles();
        indexCache.loadAllVariants();
        indexCache.loadAllTags();

        registerMapColors();

        ArchivistPlugin.log("Successfully loaded index", Level.INFO);
        ArchivistPlugin.log("Index tree:\n" + IndexCache.archivedServerSaves(), Level.INFO);

        new InventoryAPI(this).init();
        registerCommands(Map.of(
                "goto", new GoToCommand(),
                "go", new GoCommand(),
                "find", new FindCommand(),
                "reindex", new ReindexCommand(),
                "spawn", new SpawnCommand(),
                "resetinventory", new ResetInventoryCommand(),
                "phase", new PhaseCommand()
        ));
        ((Logger) LogManager.getRootLogger()).addFilter(new LogSpamFilter());
    }

    public void onDisable() {
        unloadAllBlobs();
    }

    private void registerMapColors() {
        try (final InputStream in = getClass().getResourceAsStream("/map_colors.txt")) {
            assert in != null;
            mapColorTable.registerColors(in);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands(@NotNull final Map<String, CommandExecutor> commandExecutorMap) {
        commandExecutorMap.forEach((s, c) -> Objects.requireNonNull(getCommand(s)).setExecutor(c));
    }

    public void unloadAllBlobs() {
        indexCache.collectBlobs().forEach(world -> Bukkit.unloadWorld(world, false));
    }

    @NotNull
    public static ArchivistPlugin instance() {
        return INSTANCE;
    }

    @NotNull
    public static World spawnDimension() {
        return ArchivistPlugin.instance().spawnWorld().spawnDimension();
    }

    public static boolean isAtSpawn(@NotNull final World world) {
        return world.equals(spawnDimension());
    }

    public static boolean isAtSpawn(@NotNull final Location location) {
        return isAtSpawn(location.getWorld());
    }

    public static void log(@NotNull final String s, @NotNull final Level level) {
        instance().getLogger().log(level, s);
    }

    @NotNull
    public IndexCache indexCache() {
        return indexCache;
    }

    @NotNull
    public MapColorTable mapColorTable() {
        return mapColorTable;
    }

    @NotNull
    public SpawnWorld spawnWorld() {
        return spawnWorld;
    }

    public void registerListeners(@NotNull final Listener... listeners) {
        Arrays.stream(listeners).forEach(this::registerListener);
    }

    public void registerListener(@NotNull final Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

}
