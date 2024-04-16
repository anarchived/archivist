package org.crayne.archivist.listeners;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.gui.ContainerViewGUI;
import org.crayne.archivist.gui.ServerListGUI;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.jetbrains.annotations.NotNull;

public class FrozenWorldListener implements Listener {

    @EventHandler
    public void damageEvent(@NotNull final EntityDamageEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void hangingBreakEvent(@NotNull final HangingBreakEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void entityInteractEvent(@NotNull final PlayerInteractEntityEvent ev) {
        if (ev.getRightClicked() instanceof final ItemFrame itemFrame)
            ev.getPlayer().getInventory().addItem(itemFrame.getItem().clone());
        ev.setCancelled(true);
    }

    @EventHandler
    public void entityShootBow(@NotNull final EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow && event.getEntity() instanceof Player)
            event.setConsumeItem(false);
    }

    @EventHandler
    public void blockInteractEvent(@NotNull final PlayerInteractEvent ev) {
        final Block block = ev.getClickedBlock();
        if (block == null) {
            if (ArchivistInventory.isBrowserItem(ev.getItem()))
                new ServerListGUI(ev.getPlayer()).open();

            cancelDepletion(ev);
            return;
        }
        if (block.getType() == Material.ENDER_CHEST) {
            ev.setCancelled(true);
            return;
        }
        if (!(block.getState() instanceof final Container container)) {
            cancelDepletion(ev);
            return;
        }

        ev.setCancelled(true);

        final Inventory inventory = container.getInventory();
        new ContainerViewGUI(ev.getPlayer(), inventory, container.customName()).open();
    }

    private static void cancelDepletion(@NotNull final PlayerInteractEvent ev) {
        final Material material = ev.getMaterial();
        final boolean depletes = material == Material.ENDER_PEARL;

        if (material == Material.MAP)
            ev.setCancelled(true);

        if (!depletes) return;

        final ItemStack before = ev.getItem();
        final EquipmentSlot slot = ev.getHand();

        if (before == null || slot == null)
            return;

        final ItemStack beforeFinal = before.clone();
        final PlayerInventory inv = ev.getPlayer().getInventory();

        Bukkit.getScheduler().runTaskLater(ArchivistPlugin.instance(),
                () -> inv.setItem(slot, beforeFinal),
                1L);
    }

    @EventHandler
    public void elytraBoostEvent(@NotNull final PlayerElytraBoostEvent ev) {
        ev.setShouldConsume(false);
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
    public void itemDespawnEvent(@NotNull final ItemDespawnEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void redstoneEvent(@NotNull final BlockRedstoneEvent ev) {
        ev.setNewCurrent(ev.getOldCurrent());
    }

}
