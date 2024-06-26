package org.crayne.archivist.command.query;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.IndexingException;
import org.jetbrains.annotations.NotNull;

import static org.crayne.archivist.command.CommandUtil.errorMessage;
import static org.crayne.archivist.inventory.ArchivistInventory.mainText;
import static org.crayne.archivist.inventory.ArchivistInventory.secondaryText;

public class GoToCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(errorMessage("This command can only be used ingame."));
            return false;
        }
        if (args.length != 3) {
            sender.sendMessage(errorMessage("Usage: /goto <server> <save> <date>"));
            return false;
        }
        try {
            final Location location = ArchivistPlugin.instance()
                    .indexCache()
                    .requireSaveLocation(
                            args[0].replace("_", " "),
                            args[1].replace("_", " "),
                            args[2].replace("_", " ")
                    );
            p.teleport(location);
            p.sendMessage(mainText("Teleported you to ")
                    .append(secondaryText(args[0] + ": " + args[1] + "-" + args[2]))
                    .component());
            p.setAllowFlight(true);
            return true;
        } catch (final IndexingException e) {
            sender.sendMessage(errorMessage(e.getMessage()));
            return false;
        }
    }

}
