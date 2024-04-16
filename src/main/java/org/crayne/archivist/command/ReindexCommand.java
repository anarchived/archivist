package org.crayne.archivist.command;

import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;

public class ReindexCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!sender.isOp()) return false;

        Bukkit.getOnlinePlayers().forEach(p -> p.kick(Component.text(
                "The server has entered maintenance mode.\n" +
                        "Please be patient; it will be back soon."
        )));
        Bukkit.getWorlds().forEach(w -> Arrays.stream(w.getLoadedChunks()).forEach(Chunk::unload));
        final CachedServerIndex cachedServerIndex = ArchivistPlugin.instance().serverIndex();
        final Set<String> blobWorldFiles = cachedServerIndex.collectBlobs().keySet();
        try {
            Files.delete(CachedServerIndex.cachedServerIndexPath());
            for (final String blob : blobWorldFiles) {
                FileUtils.deleteDirectory(CachedServerIndex.rootDirectory().resolve(blob).toFile());
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Bukkit.shutdown();
        return true;
    }

}
