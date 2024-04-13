package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.cached.CachedServer;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.jetbrains.annotations.NotNull;

public class ServerListGUI extends PagedGUI {

    @NotNull
    private final CachedServerIndex serverIndex;

    public ServerListGUI(@NotNull final Player p) {
        super(p, "server-list", "" + ChatColor.BLUE + ChatColor.BOLD + "Server List");
        serverIndex = ArchivistPlugin.instance().serverIndex();
    }

    public void update(@NotNull final PaginationManager pagination) {
        for (final CachedServer server : serverIndex.cachedServers().values()) {
            pagination.addItem(new Icon(Material.DIAMOND)
                    .setName(ChatColor.GOLD + server.name())
                    .onClick(e -> new SaveListGUI(this, player, server).open()));
        }
    }

}
