package org.crayne.archivist.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ContainerViewGUI extends Gui {

    @NotNull
    private final Inventory inventory;

    public ContainerViewGUI(@NotNull final Player player, @NotNull final Inventory inventory, @Nullable final Component customName) {
        super(player, "container-view", findName(inventory, customName), inventory.getSize() / 9);
        this.inventory = inventory;
    }

    @NotNull
    private static Component findName(@NotNull final Inventory inventory, @Nullable final Component customName) {
        return Optional.ofNullable(customName).orElse(inventory.getType().defaultTitle());
    }

    public void onOpen(@NotNull final InventoryOpenEvent ev) {
        for (final ItemStack itemStack : inventory.getContents()) {
            final Icon icon = new Icon(itemStack).onClick(e -> {
                if (itemStack == null
                        || itemStack.getType() == Material.AIR
                        || itemStack.getAmount() == 0) return;

                player.getInventory().addItem(itemStack.clone());
            });
            addItem(icon);
        }
    }

}
