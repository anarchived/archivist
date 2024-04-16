package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.crayne.archivist.index.cached.CachedSave;
import org.crayne.archivist.index.cached.CachedSaveData;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.text.ChatText;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class SaveGUI extends PagedGUI {

    @NotNull
    private final CachedSave save;

    public SaveGUI(@NotNull final SaveListGUI previous, @NotNull final Player p, @NotNull final CachedSave save) {
        super(previous, p, "save", "§1§l" + save.name());
        this.save = save;
    }

    public void update(@NotNull final PaginationManager pagination) {
        final CachedSaveData data = save.data();

        for (final Map.Entry<String, World> variant : data.variantWorldsSorted()) {
            final ChatText title = ArchivistInventory.mainText(variant.getKey());
            final ChatText teleportMessage = ArchivistInventory.mainText("Teleported you to ")
                    .append(ArchivistInventory.secondaryText(save.name() + "-" + variant.getKey()));

            pagination.addItem(new Icon(Material.BOOK)
                    .setName(title.legacyText())
                    .onClick(e -> {
                        player.closeInventory();
                        player.teleport(data.position().toLocation(variant.getValue()));
                        player.sendMessage(teleportMessage.legacyText());
                    }));
        }
    }

}
