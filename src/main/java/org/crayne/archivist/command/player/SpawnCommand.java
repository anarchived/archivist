package org.crayne.archivist.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.jetbrains.annotations.NotNull;

import static org.crayne.archivist.command.CommandUtil.errorMessage;

public class SpawnCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(errorMessage("This command can only be used ingame."));
            return false;
        }
        p.sendMessage(ArchivistInventory.mainText("Teleported you to spawn.").component());
        p.teleport(ArchivistPlugin.instance().spawnWorld().spawnLocation());
        p.setAllowFlight(true);
        return true;
    }

}
