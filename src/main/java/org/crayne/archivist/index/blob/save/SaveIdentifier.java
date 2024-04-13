package org.crayne.archivist.index.blob.save;

import org.crayne.archivist.index.archive.SaveIndex;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SaveIdentifier(@NotNull String saveName, @NotNull String saveVariant,
                             @NotNull UUID uuid, @NotNull SaveIndex save) {

    @NotNull
    public String toString() {
        return saveName + "-" + saveVariant;
    }

}
