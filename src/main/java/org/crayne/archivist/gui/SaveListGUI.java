package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.crayne.archivist.gui.markdown.MarkdownBookRenderer;
import org.crayne.archivist.gui.util.LoreUtil;
import org.crayne.archivist.index.cached.CachedSave;
import org.crayne.archivist.index.cached.CachedServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveListGUI extends PagedGUI {

    @NotNull
    private final CachedServer server;

    @NotNull
    private final String query;

    public SaveListGUI(@Nullable final ServerListGUI previous, @NotNull final Player p,
                       @NotNull final CachedServer server, @NotNull final String query) {
        super(previous, p, "save-list",
                "" + ChatColor.BLUE + ChatColor.BOLD + "Bases of "
                        + server.name()
                        + (query.isEmpty() ? "" : " : Searching")
        );
        this.server = server;
        this.query = sanitizeQuery(query);
    }

    public SaveListGUI(@NotNull final Player p,
                       @NotNull final CachedServer server, @NotNull final String query) {
        this(null, p, server, query);
    }

    public SaveListGUI(@NotNull final ServerListGUI previous, @NotNull final Player p, @NotNull final CachedServer server) {
        this(previous, p, server, "");
    }

    private static String sanitizeQuery(@NotNull final String query) {
        return query.replaceAll("[_ ]", "").toLowerCase();
    }

    @NotNull
    private static final List<String> LORE_DEFAULT = List.of(
            ChatColor.YELLOW + "Left click to see archived dates",
            ChatColor.YELLOW + "Right click to see more information",
            "",
            ChatColor.GOLD + "Archived dates:"
    );

    public void update(@NotNull final PaginationManager pagination) {
        for (final CachedSave save : server.saves().values()) {
            final String nameSanitized = sanitizeQuery(save.name());
            if (!query.isEmpty() && !nameSanitized.contains(query)) continue;

            final List<String> lore = new ArrayList<>(LORE_DEFAULT);
            LoreUtil.addRemainingLines(lore, save.data()
                    .variantWorldsSorted()
                    .stream()
                    .map(Map.Entry::getKey)
                    .toList());

            pagination.addItem(new Icon(Material.BOOK)
                    .setName(ChatColor.GOLD + save.name())
                    .setLore(lore)
                    .onClick(e -> {
                        if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                            MarkdownBookRenderer.displayMarkdownToPlayer(player, save.data().markdownContent());
                            return;
                        }
                        new SaveGUI(this, player, save).open();
                    }));
        }
    }

}
