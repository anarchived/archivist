package org.crayne.archivist.index.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class VirtualMapRenderer extends MapRenderer {

    @Nullable
    private final Color @NotNull [] @NotNull [] image;
    
    public VirtualMapRenderer(@NotNull final Color @NotNull [] @NotNull [] image) {
        this.image = image;
    }
    
    public void render(@NotNull final MapView mapView, @NotNull final MapCanvas mapCanvas, @NotNull final Player player) {
        for (int x = 0; x < ExternalMap.SIZE; x++) {
            for (int y = 0; y < ExternalMap.SIZE; y++) {
                mapCanvas.setPixelColor(x, y, image[x][y]);
            }
        }
        mapView.setTrackingPosition(false);
    }
    
}
