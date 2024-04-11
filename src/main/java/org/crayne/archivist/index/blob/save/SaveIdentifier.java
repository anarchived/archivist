package org.crayne.archivist.index.blob.save;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SaveIdentifier(@NotNull String saveName, @NotNull String saveVariant,
                             @NotNull UUID uuid, @NotNull Position position) {

    @NotNull
    public String toString() {
        return saveName + "-" + saveVariant + position;
    }

}
