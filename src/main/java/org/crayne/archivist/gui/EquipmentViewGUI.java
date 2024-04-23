package org.crayne.archivist.gui;

import mc.obliviate.inventory.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.jetbrains.annotations.NotNull;

import static org.crayne.archivist.gui.ContainerViewGUI.createViewableItemIcon;

public class EquipmentViewGUI extends Gui {

    @NotNull
    private final EntityEquipment equipment;

    public EquipmentViewGUI(@NotNull final Player player, @NotNull final EntityEquipment equipment) {
        super(player, "equipment-view", ArchivistInventory.mainText("Viewing Equipment").component(), 3);
        this.equipment = equipment;
    }

    public void onOpen(@NotNull final InventoryOpenEvent ev) {
        int slot = 9;
        for (final ItemStack armorContent : equipment.getArmorContents()) {
            // reverse order, so it starts with helmet instead of boots
            // + begin in second row
            addItem(21 - slot, createViewableItemIcon(player, armorContent));
            slot++;
        }
        addItem(slot, createViewableItemIcon(player, equipment.getItemInMainHand()));
        slot++;

        addItem(slot, createViewableItemIcon(player, equipment.getItemInOffHand()));
    }

}
