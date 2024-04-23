package org.crayne.archivist.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crayne.archivist.gui.SaveListGUI;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.crayne.archivist.command.CommandUtil.errorMessage;

public class FindCommand implements CommandExecutor {

    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
                             @NotNull final String label, @NotNull final String @NotNull [] args) {
        if (!(sender instanceof final Player p)) {
            sender.sendMessage(errorMessage("This command can only be used ingame."));
            return false;
        }
        final String query = Arrays.stream(args, 0, args.length).
                collect(Collectors.joining(" "));

        new SaveListGUI(p, null, query).open();
        return true;
    }

}
