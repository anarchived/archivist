package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.command.query.SearchQuery;
import org.crayne.archivist.gui.util.LoreUtil;
import org.crayne.archivist.index.IndexFile;
import org.crayne.archivist.index.cache.SaveCache;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.index.tags.MultiTag;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.text.ChatText;
import org.crayne.archivist.text.markdown.MarkdownBookRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SaveListGUI extends PagedGUI {

    @NotNull
    private final SearchQuery searchQuery;

    public SaveListGUI(@Nullable final ServerListGUI previous, @NotNull final Player p,
                       @NotNull final SearchQuery searchQuery) {
        super(previous, p, "save-list", searchQuery.createTitle());
        this.searchQuery = searchQuery;
    }

    public SaveListGUI(@NotNull final Player p, @NotNull final SearchQuery searchQuery) {
        this(null, p, searchQuery);
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ArchivistInventory.mainText("Left click to see archived dates").legacyText(),
            ArchivistInventory.mainText("Right click to see more information").legacyText(),
            "",
            ArchivistInventory.mainText("Archived dates:").legacyText()
    );

    private static void addTagLines(@NotNull final List<String> lore, @NotNull final List<MultiTag> tags) {
        final List<String> visibleTags = tags.stream()
                .filter(MultiTag::visible)
                .map(MultiTag::toString)
                .toList();

        if (visibleTags.isEmpty()) return;

        lore.add("");
        lore.add(ArchivistInventory.mainText("Tags:").legacyText());

        LoreUtil.addRemainingLines(lore, visibleTags);
    }

    private static void addServerNameLines(@NotNull final List<String> lore, @NotNull final ServerCache server) {
        lore.add("");
        lore.add(ArchivistInventory.mainText("From: " + server.name()).legacyText());
    }

    private void addSaveToGUI(@NotNull final PaginationManager pagination,
                              @NotNull final ServerCache server,
                              @NotNull final SaveCache save) {
        final List<String> lore = new ArrayList<>(LORE_DEFAULT);
        LoreUtil.addRemainingLines(lore, save.variants().keySet());

        addServerNameLines(lore, server);
        addTagLines(lore, save.tags());

        final ChatText title = ArchivistInventory.mainText(save.name());

        pagination.addItem(new Icon(MultiTag.createIconItemStack(save.tags()))
                .setName(title.legacyText())
                .setLore(lore)
                .onClick(e -> {
                    if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                        final Optional<IndexFile> indexFile = save.index().indexFile();
                        if (indexFile.isEmpty()) return;

                        MarkdownBookRenderer.displayMarkdownToPlayer(player, indexFile.get());
                        return;
                    }
                    new SaveGUI(this, player, save).open();
                }));
    }

    public void update(@NotNull final PaginationManager pagination, @NotNull final ServerCache server) {
        if (searchQuery.isEmpty()) {
            for (final SaveCache save : server.saveCacheMap().values()) {
                addSaveToGUI(pagination, server, save);
            }
            return;
        }
        final Map<SaveCache, Integer> results = new HashMap<>();
        for (final SaveCache save : server.saveCacheMap().values()) {
            final int matches = searchQuery.matches(save);
            if (matches > 0) results.put(save, matches);
        }
        final List<SaveCache> resultsSorted = results.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey)
                .toList();

        for (final SaveCache save : resultsSorted) {
            addSaveToGUI(pagination, server, save);
        }
    }

    public void update(@NotNull final PaginationManager pagination) {
        final var serverCacheMap = ArchivistPlugin.instance().indexCache().serverCacheMap();
        final var searchingServers = searchQuery.servers();

        for (final String serverName : serverCacheMap.keySet()) {
            final ServerCache server = serverCacheMap.get(serverName);
            if (!searchingServers.isEmpty() && !searchingServers.contains(server)) continue;

            update(pagination, server);
        }

    }

}
