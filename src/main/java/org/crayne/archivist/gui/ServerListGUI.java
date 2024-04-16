package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.text.ChatText;
import org.crayne.archivist.text.markdown.MarkdownBookRenderer;
import org.crayne.archivist.gui.util.LoreUtil;
import org.crayne.archivist.index.cached.CachedServer;
import org.crayne.archivist.index.cached.CachedServerIndex;
import org.crayne.archivist.index.cached.MapUtil;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ServerListGUI extends PagedGUI {

    @NotNull
    private final CachedServerIndex serverIndex;

    public ServerListGUI(@NotNull final Player p) {
        super(p, "server-list", "§1§lServer List");
        serverIndex = ArchivistPlugin.instance().serverIndex();
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ArchivistInventory.mainText("Left click to see archived bases").legacyText(),
            ArchivistInventory.mainText("Right click to see more information").legacyText(),
            "",
            ArchivistInventory.mainText("Archived bases:").legacyText()
    );

    public void update(@NotNull final PaginationManager pagination) {
        for (final CachedServer server : serverIndex.cachedServers().values()) {
            final List<String> lore = new ArrayList<>(LORE_DEFAULT);
            LoreUtil.addRemainingLines(lore, MapUtil.sortMapByKey(server.saves()).keySet().stream().toList());

            final ChatText title = ArchivistInventory.mainText(server.name());

            pagination.addItem(new Icon(Material.BOOK)
                    .setName(title.legacyText())
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
