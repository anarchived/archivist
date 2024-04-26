package org.crayne.archivist.index.maps;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import org.crayne.archivist.ArchivistPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;

public class ExternalMap {

    public static final int SIZE = 128;

    @NotNull
    public static VirtualMapRenderer readMapFromFile(@NotNull final FileInputStream in) throws IOException {
        final NBTCompound nbtCompound = NBTReader.read(in);
        final Color[][] image = new Color[SIZE][SIZE];
        final byte[] colors = nbtCompound.getCompound("data").getByteArray("colors");
        final MapColorTable mapColorTable = ArchivistPlugin.instance().mapColorTable();

        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                final int colorId = colors[x + y * SIZE] & 0xFF;
                final Color color = mapColorTable.findColorById(colorId);
                image[x][y] = color;
            }
        }
        return new VirtualMapRenderer(image);
    }

}
