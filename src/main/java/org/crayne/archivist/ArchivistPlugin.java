package org.crayne.archivist;

import mc.obliviate.inventory.InventoryAPI;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crayne.archivist.command.GoCommand;
import org.crayne.archivist.command.GoToCommand;
import org.crayne.archivist.command.ReindexCommand;
import org.crayne.archivist.command.SpawnCommand;
import org.crayne.archivist.consolefilter.LogSpamFilter;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.listeners.ChatListener;
import org.crayne.archivist.listeners.FrozenWorldListener;
import org.crayne.archivist.world.SpawnWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class ArchivistPlugin extends JavaPlugin {

    private SpawnWorld spawnWorld;
    private CachedServerIndex serverIndex;

    private static ArchivistPlugin INSTANCE;

    public void onEnable() {
        INSTANCE = this;
        registerListeners(
                spawnWorld = new SpawnWorld(),
                new FrozenWorldListener(),
                new ArchivistInventory(),
                new ChatListener()
        );
        serverIndex = CachedServerIndex.loadServerIndex()
                .orElseThrow(() -> new IndexingException("Cannot load empty archive"));

        new InventoryAPI(this).init();
        registerCommands(Map.of(
                "goto", new GoToCommand(),
                "go", new GoCommand(),
                "reindex", new ReindexCommand(),
                "spawn", new SpawnCommand()
        ));
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new LogSpamFilter());
    }

    public void onDisable() {
        unloadAllBlobs();
    }

    private void registerCommands(@NotNull final Map<String, CommandExecutor> commandExecutorMap) {
        commandExecutorMap.forEach((s, c) -> Objects.requireNonNull(getCommand(s)).setExecutor(c));
    }

    public void unloadAllBlobs() {
        serverIndex.collectBlobs().keySet().forEach(world -> Bukkit.unloadWorld(world, false));
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
