package org.crayne.archivist.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.index.maps.VirtualMapRenderer;
import org.crayne.archivist.text.ChatText;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class MapLoadListener implements Listener {

    @NotNull
    private static final Map<Integer, MapView> MAP_REMAPPING = new ConcurrentHashMap<>();

    @EventHandler
    public void mapLoadEvent(@NotNull final EntitiesLoadEvent ev) {
        final var blobWorldMap = ArchivistPlugin.instance().indexCache().collectBlobWorldMap();
        final World world = ev.getWorld();
        final ServerCache serverCache = blobWorldMap.get(world);

        final List<Runnable> executeInSync = new ArrayList<>();

        final ExecutorService remapperThreadPool = Executors.newCachedThreadPool();
        for (final Entity entity : ev.getEntities()) {
            if (!(entity instanceof final ItemFrame itemFrame)) continue;

            final ItemStack item = itemFrame.getItem();

            remapperThreadPool.execute(() -> remapSingle(item, world, serverCache).ifPresent(runnable -> {
                synchronized (executeInSync) {
                    executeInSync.add(runnable);
                    executeInSync.add(() -> itemFrame.setItem(item, false));
                }
            }));
        }
        remapperThreadPool.shutdown();
        try {
            final boolean successful = remapperThreadPool.awaitTermination(1, TimeUnit.MINUTES);
            if (!successful) {
                ArchivistPlugin.log("Could not remap map data at " + ev.getChunk() + " in under 1 minute", Level.SEVERE);
                return;
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
        Bukkit.getScheduler().runTask(ArchivistPlugin.instance(),
                () -> executeInSync.forEach(Runnable::run)
        );
    }

    @NotNull
    public static Optional<Runnable> remapSingle(@NotNull final ItemStack item, @NotNull final World world,
                                                 @NotNull final ServerCache serverCache) {
        final ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof final MapMeta mapMeta))
            return Optional.empty();

        if (meta.hasCustomModelData()) {
            final int oldMapId = meta.getCustomModelData();
            return remapItem(item, oldMapId, mapMeta, world, serverCache, false);
        }
        final int oldMapId = mapMeta.getMapId();
        return remapItem(item, oldMapId, mapMeta, world, serverCache, true);
    }

    @NotNull
    private static Optional<Runnable> remapItem(@NotNull final ItemStack item, final int oldMapId,
                                                @NotNull final MapMeta mapMeta, @NotNull final World world,
                                                @NotNull final ServerCache serverCache,
                                                final boolean applyOldInformation) {
        final int mapIdHash = Objects.hash(serverCache.name(), oldMapId);

        if (MAP_REMAPPING.containsKey(mapIdHash)) return Optional.of(() -> {
            final MapView mapView = MAP_REMAPPING.get(mapIdHash);

            assert mapView != null;
            applyMapView(item, mapMeta, mapView, oldMapId, applyOldInformation);
        });
        return loadRenderer(oldMapId, serverCache).map(virtualMapRenderer -> () -> {
            final MapView mapView = !applyOldInformation ? Bukkit.getMap(mapMeta.getMapId()) : Bukkit.createMap(world);
            assert mapView != null;
            MAP_REMAPPING.put(mapIdHash, mapView);

            applyMapView(item, mapMeta, mapView, oldMapId, applyOldInformation);
            mapView.addRenderer(virtualMapRenderer);
        });
    }

    private static void applyMapView(@NotNull final ItemStack item, @NotNull final MapMeta mapMeta,
                                     @NotNull final MapView mapView, final int oldMapId,
                                     final boolean applyOldInformation) {
        if (applyOldInformation) {
            mapMeta.setCustomModelData(oldMapId);
            final List<Component> lore = Optional.ofNullable(mapMeta.lore()).orElse(new ArrayList<>());

            lore.add(ChatText.text("§6Real Id: #" + oldMapId).component());
            mapMeta.lore(lore);
        }
        mapMeta.setMapView(mapView);
        item.setItemMeta(mapMeta);
    }

    @NotNull
    private static Optional<VirtualMapRenderer> loadRenderer(final int oldMapId, @NotNull final ServerCache serverCache) {
        final Optional<VirtualMapRenderer> mapRenderer = serverCache.loadMapView(oldMapId);
        if (mapRenderer.isEmpty()) {
            ArchivistPlugin.log("Could not load map id " + serverCache.name() + ":" + oldMapId, Level.WARNING);
            return Optional.empty();
        }
        return mapRenderer;
    }

}
