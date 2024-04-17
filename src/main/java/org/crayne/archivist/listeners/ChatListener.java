package org.crayne.archivist.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener {

    @NotNull
    private static final Component
            NAME_PREFIX = Component.text("<"),
            NAME_SUFFIX = Component.text("> ");

    @EventHandler
    public void asyncChatEvent(@NotNull final AsyncChatEvent ev) {
        ev.setCancelled(true);
        final Component name = ev.getPlayer().name().color(NamedTextColor.GRAY);
        final Component messagePrefix = NAME_PREFIX.append(name).append(NAME_SUFFIX);

        Bukkit.broadcast(messagePrefix.append(ev.message()));
    }

}
