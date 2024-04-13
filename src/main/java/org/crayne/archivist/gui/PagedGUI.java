package org.crayne.archivist.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PagedGUI extends Gui {

    private static final int PAGE_SELECTOR = 53;
    private static final int PREVIOUS_WINDOW = 45;

    @NotNull
    private final PaginationManager pagination = new PaginationManager(this);

    @Nullable
    private final Gui previous;

    public PagedGUI(@Nullable final Gui previous, @NotNull final Player p,
                    @NotNull final String id, @NotNull final String title) {
        super(p, id, title, 6);
        pagination.registerPageSlotsBetween(0, 52);
        this.previous = previous;
    }

    public PagedGUI(@NotNull final Player p,
                    @NotNull final String id, @NotNull final String title) {
        this(null, p, id, title);
    }

    public void onOpen(@NotNull final InventoryOpenEvent ev) {
        updateDisplayItems();
        final Icon pageIcon = new Icon(Material.ARROW)
                .setName(ChatColor.GOLD + "Change Page")
                .setLore(ChatColor.YELLOW + "Left click for next page",
                         ChatColor.YELLOW + "Right click for previous page"
                );

        final Icon previousWindowIcon = new Icon(Material.SPECTRAL_ARROW)
                .setName(ChatColor.GOLD + "Previous window");

        addItem(PAGE_SELECTOR, pageIcon.onClick(e -> {
            if (updatePage(e))
                updateDisplayItems();
        }));

        if (previous != null)
            addItem(PREVIOUS_WINDOW, previousWindowIcon.onClick(e -> previous.open()));
    }

    public boolean updatePage(@NotNull final InventoryClickEvent ev) {
        return switch (ev.getClick()) {
            case LEFT, SHIFT_LEFT -> {
                if (pagination.isLastPage()) {
                    yield false;
                }
                pagination.goNextPage();
                yield true;
            }
            case RIGHT, SHIFT_RIGHT -> {
                if (pagination.isFirstPage()) {
                    yield false;
                }
                pagination.goPreviousPage();
                yield true;
            }
            default -> false;
        };
    }

    public abstract void update(@NotNull final PaginationManager pagination);

    public void updateDisplayItems() {
        pagination.getItems().clear();
        update(pagination);
        pagination.update();
    }
}
