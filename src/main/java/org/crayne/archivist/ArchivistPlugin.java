package org.crayne.archivist;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crayne.archivist.command.GoToCommand;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.crayne.archivist.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.logging.Level;

public class ArchivistPlugin extends JavaPlugin {

    private SpawnWorld spawnWorld;
    private CachedServerIndex serverIndex;

    private static ArchivistPlugin INSTANCE;

    public void onEnable() {
        INSTANCE = this;

        spawnWorld = new SpawnWorld();
        registerListeners(
                spawnWorld
        );
        serverIndex = CachedServerIndex.loadServerIndex()
                .orElseThrow(() -> new IndexingException("Cannot load empty archive"));

        getCommand("goto").setExecutor(new GoToCommand());
    }

    public void onDisable() {

    }

    @NotNull
    public static ArchivistPlugin instance() {
        return INSTANCE;
    }

    public static void log(@NotNull final String s, @NotNull final Level level) {
        instance().getLogger().log(level, s);
    }

    @NotNull
    public CachedServerIndex serverIndex() {
        return serverIndex;
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
