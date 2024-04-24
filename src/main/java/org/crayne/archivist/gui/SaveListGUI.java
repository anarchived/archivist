package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.ArchivistPlugin;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaveListGUI extends PagedGUI {

    @Nullable
    private final ServerCache server;

    @NotNull
    private final String query;

    public SaveListGUI(@Nullable final ServerListGUI previous, @NotNull final Player p,
                       @Nullable final ServerCache server, @NotNull final String query) {
        super(previous, p, "save-list", "§1§l" + createTitle(server, query));
        this.server = server;
        this.query = sanitizeQuery(query);
    }

    public SaveListGUI(@NotNull final Player p,
                       @Nullable final ServerCache server, @NotNull final String query) {
        this(null, p, server, query);
    }

    public SaveListGUI(@NotNull final ServerListGUI previous, @NotNull final Player p,
                       @Nullable ServerCache server) {
        this(previous, p, server, "");
    }

    @NotNull
    private static String createTitle(@Nullable final ServerCache server, @NotNull final String query) {
        final String lookupMode = query.isEmpty() ? "Bases of" : "Searching";
        if (server != null)
            return lookupMode + " " + server.name();

        return lookupMode + " Archive";
    }

    @NotNull
    private static String sanitizeQuery(@NotNull final String query) {
        return query.replaceAll("[_ ]", "").toLowerCase();
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ArchivistInventory.mainText("Left click to see archived dates").legacyText(),
            ArchivistInventory.mainText("Right click to see more information").legacyText(),
            "",
            ArchivistInventory.mainText("Archived dates:").legacyText()
    );

    private static void addTagLines(@NotNull final List<String> lore, @NotNull final List<MultiTag> tags) {
        if (tags.isEmpty()) return;

        lore.add("");
        lore.add(ArchivistInventory.mainText("Tags:").legacyText());
        LoreUtil.addRemainingLines(lore, tags.stream().map(MultiTag::toString).toList());
    }

    private static void addServerNameLines(@NotNull final List<String> lore, @NotNull final ServerCache server) {
        lore.add("");
        lore.add(ArchivistInventory.mainText("From: " + server.name()).legacyText());
    }

    private void filterSave(@NotNull final PaginationManager pagination,
                            @NotNull final ServerCache server,
                            @NotNull final SaveCache save) {
        final String nameSanitized = sanitizeQuery(save.name());
        if (!query.isEmpty() && !nameSanitized.contains(query)) return;

        final List<String> lore = new ArrayList<>(LORE_DEFAULT);
        LoreUtil.addRemainingLines(lore, save.variants().keySet());

        addServerNameLines(lore, server);
        addTagLines(lore, save.tags());

        final ChatText title = ArchivistInventory.mainText(save.name());

        pagination.addItem(new Icon(Material.BOOK)
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
        for (final SaveCache save : server.saveCacheMap().values()) {
            filterSave(pagination, server, save);
        }
    }

    public void update(@NotNull final PaginationManager pagination) {
        if (server != null) {
            update(pagination, server);
            return;
        }
        final var serverCacheMap = ArchivistPlugin.instance().indexCache().serverCacheMap();
        for (final String serverName : serverCacheMap.keySet()) {
            final ServerCache server = serverCacheMap.get(serverName);
            update(pagination, server);
        }

    }

}
