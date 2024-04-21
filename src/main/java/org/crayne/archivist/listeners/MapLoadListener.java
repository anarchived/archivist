package org.crayne.archivist.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.index.maps.VirtualMapRenderer;
import org.crayne.archivist.text.ChatText;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class MapLoadListener implements Listener {

    @NotNull
    private static final Map<Integer, MapView> MAP_REMAPPING = new HashMap<>();

    @EventHandler
    public void mapLoadEvent(@NotNull final ChunkLoadEvent ev) {
        final Chunk chunk = ev.getChunk();

        final var blobWorldMap = ArchivistPlugin.instance().indexCache().collectBlobWorldMap();
        final World world = chunk.getWorld();
        final ServerCache serverCache = blobWorldMap.get(world);

        for (final Entity entity : chunk.getEntities()) {
            if (!(entity instanceof final ItemFrame itemFrame)) continue;

            final ItemStack item = remapRecursively(itemFrame.getItem(), world, serverCache);
            itemFrame.setItem(item, false);
        }
    }

    public static void remapInventory(@NotNull final Inventory inventory, @NotNull final World world) {
        final var blobWorldMap = ArchivistPlugin.instance().indexCache().collectBlobWorldMap();
        final ServerCache serverCache = blobWorldMap.get(world);

        for (final ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            remapRecursively(itemStack, world, serverCache);
        }
    }

    @NotNull
    public static ItemStack remapRecursively(@NotNull final ItemStack item, @NotNull final World world,
                                             @NotNull final ServerCache serverCache) {
        final ItemMeta meta = item.getItemMeta();
        if (meta instanceof final BlockStateMeta blockStateMeta
                && blockStateMeta.getBlockState() instanceof final Container container) {

            remapInventory(container.getInventory(), world);
            blockStateMeta.setBlockState(container);
            item.setItemMeta(blockStateMeta);
            return item;
        }
        remapSingle(item, world, serverCache);
        return item;
    }

    private static void remapSingle(@NotNull final ItemStack item, @NotNull final World world,
                                    @NotNull final ServerCache serverCache) {
        final ItemMeta meta = item.getItemMeta();

        if (!(meta instanceof final MapMeta mapMeta))
            return;

        if (meta.hasCustomModelData()) {
            final int oldMapId = meta.getCustomModelData();
            remapItem(item, oldMapId, mapMeta, world, serverCache, false);
            return;
        }
        final int oldMapId = mapMeta.getMapId();
        remapItem(item, oldMapId, mapMeta, world, serverCache, true);
    }

    private static void remapItem(@NotNull final ItemStack item, final int oldMapId,
                                  @NotNull final MapMeta mapMeta, @NotNull final World world,
                                  @NotNull final ServerCache serverCache,
                                  final boolean applyOldInformation) {
        final int mapIdHash = Objects.hash(serverCache.name(), oldMapId);

        if (MAP_REMAPPING.containsKey(mapIdHash)) {
            final MapView mapView = MAP_REMAPPING.get(mapIdHash);

            assert mapView != null;
            applyMapView(item, mapMeta, mapView, oldMapId, applyOldInformation);
            return;
        }
        final Optional<VirtualMapRenderer> renderer = loadRenderer(oldMapId, world, serverCache);
        if (renderer.isEmpty()) return;

        final MapView mapView = !applyOldInformation ? Bukkit.getMap(mapMeta.getMapId()) : Bukkit.createMap(world);

        assert mapView != null;
        applyMapView(item, mapMeta, mapView, oldMapId, applyOldInformation);

        mapView.addRenderer(renderer.get());
        MAP_REMAPPING.put(mapIdHash, mapView);
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
    private static Optional<VirtualMapRenderer> loadRenderer(final int oldMapId, @NotNull final World world,
                                                             @NotNull final ServerCache serverCache) {
        final Optional<VirtualMapRenderer> mapRenderer = serverCache.loadMapView(oldMapId, world);
        if (mapRenderer.isEmpty()) {
            ArchivistPlugin.log("Could not load map id " + serverCache.name() + ":" + oldMapId, Level.WARNING);
            return Optional.empty();
        }
        return mapRenderer;
    }

}
