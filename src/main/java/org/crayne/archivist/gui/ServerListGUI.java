package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.command.query.SearchQuery;
import org.crayne.archivist.gui.util.LoreUtil;
import org.crayne.archivist.index.IndexFile;
import org.crayne.archivist.index.cache.IndexCache;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.index.tags.MultiTag;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.text.ChatText;
import org.crayne.archivist.text.markdown.MarkdownBookRenderer;
import org.crayne.archivist.util.MapUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ServerListGUI extends PagedGUI {

    @NotNull
    private final IndexCache indexCache;

    public ServerListGUI(@NotNull final Player p) {
        super(p, "server-list", "§1§lServer List");
        indexCache = ArchivistPlugin.instance().indexCache();
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ArchivistInventory.mainText("Left click to see archived bases").legacyText(),
            ArchivistInventory.mainText("Right click to see more information").legacyText(),
            "",
            ArchivistInventory.mainText("Archived bases:").legacyText()
    );

    public void update(@NotNull final PaginationManager pagination) {
        for (final ServerCache server : indexCache.serverCacheMap().values()) {
            final List<String> lore = new ArrayList<>(LORE_DEFAULT);
            LoreUtil.addRemainingLines(lore, MapUtil.sortMapByKey(server.saveCacheMap()).keySet().stream().toList());

            final ChatText title = ArchivistInventory.mainText(server.name());

            pagination.addItem(new Icon(MultiTag.createIconItemStack(server.tags()))
                    .setName(title.legacyText())
                    .setLore(lore)
                    .onClick(e -> {
                        if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                            final Optional<IndexFile> indexFile = server.index().indexFile();
                            if (indexFile.isEmpty()) return;

                            MarkdownBookRenderer.displayMarkdownToPlayer(player, indexFile.get());
                            return;
                        }
                        new SaveListGUI(this, player, SearchQuery.query(Set.of(server))).open();
                    }));
        }
    }

}
