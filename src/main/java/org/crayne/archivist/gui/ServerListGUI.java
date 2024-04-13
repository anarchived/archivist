package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.gui.markdown.MarkdownBookRenderer;
import org.crayne.archivist.gui.util.LoreUtil;
import org.crayne.archivist.index.cached.CachedServer;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.crayne.archivist.index.cached.MapUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerListGUI extends PagedGUI {

    @NotNull
    private final CachedServerIndex serverIndex;

    public ServerListGUI(@NotNull final Player p) {
        super(p, "server-list", "" + ChatColor.BLUE + ChatColor.BOLD + "Server List");
        serverIndex = ArchivistPlugin.instance().serverIndex();
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ChatColor.YELLOW + "Left click to see archived bases",
            ChatColor.YELLOW + "Right click to see more information",
            "",
            ChatColor.GOLD + "Archived bases:"
    );

    public void update(@NotNull final PaginationManager pagination) {
        for (final CachedServer server : serverIndex.cachedServers().values()) {
            final List<String> lore = new ArrayList<>(LORE_DEFAULT);
            LoreUtil.addRemainingLines(lore, MapUtil.sortMapByKey(server.saves()).keySet().stream().toList());

            pagination.addItem(new Icon(Material.BOOK)
                    .setName(ChatColor.GOLD + server.name())
                    .setLore(lore)
                    .onClick(e -> {
                        if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                            MarkdownBookRenderer.displayMarkdownToPlayer(player, server.markdownContent());
                            return;
                        }
                        new SaveListGUI(this, player, server).open();
                    }));
        }
    }

}
