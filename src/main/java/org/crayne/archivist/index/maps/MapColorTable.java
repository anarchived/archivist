package org.crayne.archivist.index.maps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class MapColorTable {

    @NotNull
    private final Map<Integer, Color> mapColorTable;

    public MapColorTable() {
        mapColorTable = new ConcurrentHashMap<>();
    }

    @Nullable
    public Color findColorById(final int id) {
        return mapColorTable.get(id);
    }

    public void registerColor(final int id, @Nullable final Color color) {
        mapColorTable.put(id, color);
    }

    public void registerColor(@NotNull final String line) {
        final String[] split = line.split(" ");
        if (split.length != 4) return;

        try {
            final int id = Integer.parseInt(split[0]);
            final int r = Integer.parseInt(split[1]);
            final int g = Integer.parseInt(split[2]);
            final int b = Integer.parseInt(split[3]);

            registerColor(id, new Color(r, g, b));
        } catch (final NumberFormatException ignored) {}
    }

    public void registerColors(@NotNull final Stream<String> lines) {
        lines.forEach(this::registerColor);
    }

    public void registerColors(@NotNull final InputStream in) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        registerColors(reader.lines());
    }

}
