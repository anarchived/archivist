package org.crayne.archivist.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.jetbrains.annotations.NotNull;

public class GoToCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used ingame.");
            return false;
        }
        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /goto <server> <save> <date>");
            return false;
        }
        try {
            final Location location = ArchivistPlugin.instance()
                    .serverIndex()
                    .findSaveLocation(
                            args[0].replace("_", " "),
                            args[1].replace("_", " "),
                            args[2].replace("_", " ")
                    );
            p.teleport(location);
            p.sendMessage(ChatColor.GOLD + "Teleported you to " + args[0] + ": " + args[1] + "-" + args[2]);
            return true;
        } catch (final IndexingException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            return false;
        }
    }

}
