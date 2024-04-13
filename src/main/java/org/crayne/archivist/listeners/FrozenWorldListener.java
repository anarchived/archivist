package org.crayne.archivist.listeners;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.crayne.archivist.gui.ContainerViewGUI;
import org.jetbrains.annotations.NotNull;

public class FrozenWorldListener implements Listener {

    @EventHandler
    public void damageEvent(@NotNull final EntityDamageEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void entityInteractEvent(@NotNull final PlayerInteractAtEntityEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockInteractEvent(@NotNull final PlayerInteractEvent ev) {
        final Block block = ev.getClickedBlock();
        if (block == null) return;

        ev.setCancelled(true);
        if (block.getType() == Material.ENDER_CHEST) return;

        if (!(block.getState() instanceof final Container container)) return;
        if (!(block.getState() instanceof final Nameable nameable)) return;

        final Inventory inventory = container.getInventory();
        new ContainerViewGUI(ev.getPlayer(), inventory, nameable.getCustomName()).open();
    }

    @EventHandler
    public void joinEvent(@NotNull final PlayerJoinEvent ev) {
        final Player p = ev.getPlayer();
        p.setFoodLevel(20);
        p.setHealth(20);
    }

    @EventHandler
    public void dropEvent(@NotNull final PlayerDropItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void hungerEvent(@NotNull final FoodLevelChangeEvent ev) {
        ev.setCancelled(true);
        ev.setFoodLevel(20);
    }

    @EventHandler
    public void chunkLoadEvent(@NotNull final ChunkLoadEvent ev) {
        for (final Entity entity : ev.getChunk().getEntities()) {
            if (!(entity instanceof final LivingEntity livingEntity)) continue;

            livingEntity.setAI(false);
        }
    }

    @EventHandler
    public void entityPathfindEvent(@NotNull final EntityPathfindEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void entityTargetEvent(@NotNull final EntityTargetEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void spawnEvent(@NotNull final EntitySpawnEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void itemDespawnEvent(@NotNull final ItemDespawnEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void redstoneEvent(@NotNull final BlockRedstoneEvent ev) {
        ev.setNewCurrent(ev.getOldCurrent());
    }

}
