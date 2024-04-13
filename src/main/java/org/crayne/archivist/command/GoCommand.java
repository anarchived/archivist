package org.crayne.archivist.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.gui.SaveListGUI;
import org.crayne.archivist.gui.ServerListGUI;
import org.crayne.archivist.index.cached.CachedServer;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class GoCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used ingame.");
            return false;
        }
        if (args.length == 0) {
            new ServerListGUI(p).open();
            return true;
        }
        final Optional<CachedServer> server = requireServer(args, p);
        if (server.isEmpty()) return false;

        final String query = Arrays.stream(args, 1, args.length).
                collect(Collectors.joining(" "));

        new SaveListGUI(p, server.get(), query).open();
        return true;
    }

    @NotNull
    private static Optional<CachedServer> requireServer(@NotNull final String[] args, @NotNull final Player p) {
        final CachedServerIndex serverIndex = ArchivistPlugin.instance().serverIndex();
        final String serverName = args[0];
        final CachedServer server = serverIndex.cachedServers().get(serverName);
        if (server == null) {
            p.sendMessage(ChatColor.RED + "Could not find the server '" + serverName + "'");
            return Optional.empty();
        }
        return Optional.of(server);
    }

}
