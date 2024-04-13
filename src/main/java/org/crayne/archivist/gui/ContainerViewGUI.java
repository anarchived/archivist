package org.crayne.archivist.gui;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainerViewGUI extends Gui {

    @NotNull
    private final Inventory inventory;

    public ContainerViewGUI(@NotNull final Player player, @NotNull final Inventory inventory, @Nullable final String customName) {
        super(player, "container-view", findName(inventory, customName), inventory.getSize() / 9);
        this.inventory = inventory;
    }

    @NotNull
    private static String findName(@NotNull final Inventory inventory, @Nullable final String customName) {
        if (customName == null) {
            return switch (inventory.getType()) {
                case CHEST -> "Chest";
                case DISPENSER -> "Dispenser";
                case DROPPER -> "Dropper";
                case FURNACE -> "Furnace";
                case BREWING -> "Brewing Stand";
                case BEACON -> "Beacon";
                case HOPPER -> "Hopper";
                case SHULKER_BOX -> "Shulker Box";
                default -> inventory.getName();
            };
        }
        return customName;
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
