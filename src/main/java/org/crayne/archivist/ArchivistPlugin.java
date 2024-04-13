package org.crayne.archivist;

import mc.obliviate.inventory.InventoryAPI;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crayne.archivist.command.GoCommand;
import org.crayne.archivist.command.GoToCommand;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.crayne.archivist.listeners.FrozenWorldListener;
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
        registerListeners(
                spawnWorld = new SpawnWorld(),
                new FrozenWorldListener()
        );
        serverIndex = CachedServerIndex.loadServerIndex()
                .orElseThrow(() -> new IndexingException("Cannot load empty archive"));

        new InventoryAPI(this).init();
        getCommand("goto").setExecutor(new GoToCommand());
        getCommand("go").setExecutor(new GoCommand());
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
