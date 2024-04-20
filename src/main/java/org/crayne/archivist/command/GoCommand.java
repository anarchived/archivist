package org.crayne.archivist.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.gui.SaveListGUI;
import org.crayne.archivist.gui.ServerListGUI;
import org.crayne.archivist.index.cache.IndexCache;
import org.crayne.archivist.index.cache.ServerCache;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.crayne.archivist.command.CommandUtil.errorMessage;

public class GoCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(errorMessage("This command can only be used ingame."));
            return false;
        }
        if (args.length == 0) {
            new ServerListGUI(p).open();
            return true;
        }
        final Optional<ServerCache> server = requireServer(args[0], p);
        if (server.isEmpty()) return false;

        final String query = Arrays.stream(args, 1, args.length).
                collect(Collectors.joining(" "));

        new SaveListGUI(p, server.get(), query).open();
        return true;
    }

    @NotNull
    public static Optional<ServerCache> requireServer(@NotNull final String serverName, @NotNull final Player p) {
        final IndexCache indexCache = ArchivistPlugin.instance().indexCache();
        final Optional<ServerCache> server = indexCache.resolveServerCache(serverName);

        if (server.isEmpty()) {
            p.sendMessage(errorMessage("Could not find the server '" + serverName + "'"));
            return Optional.empty();
        }
        return server;
    }

}
