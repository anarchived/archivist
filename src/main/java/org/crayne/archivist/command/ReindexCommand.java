package org.crayne.archivist.command;

import net.kyori.adventure.text.Component;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.crayne.archivist.index.cache.IndexCache;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

public class ReindexCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!sender.isOp()) return false;

        Bukkit.getOnlinePlayers().forEach(p -> p.kick(Component.text(
                "The server has entered maintenance mode.\n" +
                        "Please be patient; it will be back soon."
        )));
        ArchivistPlugin.instance().unloadAllBlobs();
        final IndexCache indexCache = ArchivistPlugin.instance().indexCache();
        final Set<World> blobWorlds  = indexCache.collectBlobs();
        try {
            for (final World world : blobWorlds) {
                FileUtils.deleteDirectory(IndexCache.rootDirectory().resolve(world.getName()).toFile());
            }
        } catch (final IOException e) {
            throw new IndexingException(e);
        }
        Bukkit.shutdown();
        return true;
    }

}
