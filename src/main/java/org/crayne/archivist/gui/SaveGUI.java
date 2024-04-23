package org.crayne.archivist.gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.crayne.archivist.index.cache.SaveCache;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.text.ChatText;
import org.crayne.archivist.util.world.Position;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class SaveGUI extends PagedGUI {

    @NotNull
    private final SaveCache save;

    public SaveGUI(@NotNull final SaveListGUI previous, @NotNull final Player p, @NotNull final SaveCache save) {
        super(previous, p, "save", "§1§l" + save.name());
        this.save = save;
    }

    public void update(@NotNull final PaginationManager pagination) {
        for (final Map.Entry<String, World> variant : save.variants().entrySet()) {
            final Optional<Position> position = save.position();
            if (position.isEmpty()) return;

            final ChatText title = ArchivistInventory.mainText(variant.getKey());
            final ChatText teleportMessage = ArchivistInventory.mainText("Teleported you to ")
                    .append(ArchivistInventory.secondaryText(save.name() + "-" + variant.getKey()));

            pagination.addItem(new Icon(Material.BOOK)
                    .setName(title.legacyText())
                    .onClick(e -> {
                        player.closeInventory();
                        player.teleport(position.get().toLocation(variant.getValue()));
                        player.setAllowFlight(true);
                        player.sendMessage(teleportMessage.legacyText());
                    }));
        }
    }

}
