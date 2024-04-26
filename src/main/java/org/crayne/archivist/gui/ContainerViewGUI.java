package org.crayne.archivist.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.listeners.MapLoadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ContainerViewGUI extends Gui {

    @NotNull
    private final Inventory inventory;

    public ContainerViewGUI(@NotNull final Player player, @NotNull final Inventory inventory, @Nullable final Component customName) {
        super(player, "container-view", findName(inventory, customName), (int) Math.max(Math.ceil(inventory.getSize() / 9.0), 1));
        this.inventory = inventory;
    }

    @NotNull
    private static Component findName(@NotNull final Inventory inventory, @Nullable final Component customName) {
        return Optional.ofNullable(customName).orElse(inventory.getType().defaultTitle());
    }

    @NotNull
    protected static Icon createViewableItemIcon(@NotNull final Player viewer, @Nullable final ItemStack itemStack) {
        final World world = viewer.getWorld();
        final ServerCache serverCache = ArchivistPlugin.instance()
                .indexCache()
                .collectBlobWorldMap()
                .get(world);

        return new Icon(itemStack).onClick(e -> {
            if (itemStack == null
                    || itemStack.getType() == Material.AIR
                    || itemStack.getAmount() == 0) return;

            final ItemStack item = itemStack.clone();
            MapLoadListener.remapSingle(item, world, serverCache).ifPresent(Runnable::run);
            viewer.setItemOnCursor(new ItemStack(Material.AIR));
            viewer.getInventory().addItem(item);
        });
    }

    public void onOpen(@NotNull final InventoryOpenEvent ev) {
        int slot = 0;
        for (final ItemStack itemStack : inventory.getContents()) {
            addItem(slot, createViewableItemIcon(player, itemStack));
            slot++;
        }
    }

}
