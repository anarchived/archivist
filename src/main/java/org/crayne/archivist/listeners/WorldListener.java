package org.crayne.archivist.listeners;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.crayne.archivist.ArchivistPlugin;
import org.crayne.archivist.command.GoCommand;
import org.crayne.archivist.gui.ContainerViewGUI;
import org.crayne.archivist.gui.SaveListGUI;
import org.crayne.archivist.gui.ServerListGUI;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.inventory.ArchivistInventory;
import org.crayne.archivist.util.world.BlockPosition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class WorldListener implements Listener {

    @EventHandler
    public void damageEvent(@NotNull final EntityDamageEvent ev) {
        if (ev.getCause() != EntityDamageEvent.DamageCause.KILL)
            ev.setCancelled(true);
    }

    @EventHandler
    public void hangingBreakEvent(@NotNull final HangingBreakEvent ev) {
        ev.setCancelled(true);
    }

    // too lazy to make a config for this currently
    @NotNull
    private static final Map<BlockPosition, String> INTERACTABLES_SERVER_NAME_MAP = Map.of(
            new BlockPosition(-10, 66, 10), "0b0t.org"
    );

    private static void handleSpawnInteraction(@NotNull final Player p, @NotNull final Entity entity) {
        final Location location = entity.getLocation();

        if (!location.getWorld().equals(ArchivistPlugin.instance().spawnWorld().spawnWorld())) return;

        final BlockPosition blockPosition = BlockPosition.of(location);
        final String serverName = INTERACTABLES_SERVER_NAME_MAP.get(blockPosition);
        if (serverName == null) return;

        final Optional<ServerCache> server = GoCommand.requireServer(serverName, p);
        if (server.isEmpty()) return;

        new SaveListGUI(p, server.get(), "").open();
    }

    @EventHandler
    public void entityInteractEvent(@NotNull final PlayerInteractEntityEvent ev) {
        final Player p = ev.getPlayer();
        if (p.getGameMode() == GameMode.CREATIVE) return;

        final Entity entity = ev.getRightClicked();

        if (entity instanceof final ItemFrame itemFrame)
            p.getInventory().addItem(itemFrame.getItem().clone());

        ev.setCancelled(true);
        handleSpawnInteraction(p, entity);
    }

    @EventHandler
    public void entityShootBow(@NotNull final EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow && event.getEntity() instanceof Player)
            event.setConsumeItem(false);
    }

    @EventHandler
    public void entityExplodeEvent(@NotNull final EntityExplodeEvent ev) {
        ev.setCancelled(true);
    }

    private static void handleAirRightClick(@NotNull final PlayerInteractEvent ev, @NotNull final Player p,
                                            @Nullable final ItemStack item) {
        if (ArchivistInventory.isBrowserItem(item))
            new ServerListGUI(p).open();

        if (item != null && item.getItemMeta() instanceof final BlockStateMeta meta
                && meta.getBlockState() instanceof final ShulkerBox shulkerBox) {
            new ContainerViewGUI(p, shulkerBox.getInventory(), meta.displayName()).open();
        }
        cancelDepletion(ev);
    }

    private static boolean handleSignRightClick(@NotNull final PlayerInteractEvent ev,
                                                @NotNull final BlockData data) {
        if (data instanceof Sign) {
            ev.setCancelled(true);
            return true;
        }
        return false;
    }

    private static boolean handleDisallowedRightClick(@NotNull final PlayerInteractEvent ev,
                                                      @NotNull final BlockData data) {
        if (data instanceof EnderChest || data instanceof Door
                || data instanceof TrapDoor) {
            ev.setCancelled(true);
            return true;
        }
        return false;
    }

    private static void handleContainerRightClick(@NotNull final PlayerInteractEvent ev,
                                                  @NotNull final Player p,
                                                  @NotNull final Block block) {
        final BlockState state = block.getState();
        if (!(state instanceof final Container container)) {
            cancelDepletion(ev);
            return;
        }
        ev.setCancelled(true);

        final Inventory inventory = container.getInventory();
        new ContainerViewGUI(p, inventory, container.customName()).open();
    }

    @EventHandler
    public void blockInteractEvent(@NotNull final PlayerInteractEvent ev) {
        final Block block = ev.getClickedBlock();
        final Player p = ev.getPlayer();
        final ItemStack item = ev.getItem();
        final Action action = ev.getAction();

        if (block == null) {
            if (action != Action.RIGHT_CLICK_AIR) return;

            handleAirRightClick(ev, p, item);
            return;
        }
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        final BlockData data = block.getBlockData();

        if (handleSignRightClick(ev, data)) return;
        if (p.getGameMode() == GameMode.CREATIVE) return;
        if (handleDisallowedRightClick(ev, data)) return;

        handleContainerRightClick(ev, p, block);
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
    public void projectileHitEvent(@NotNull final ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof EnderPearl)) ev.setCancelled(true);
    }

    @EventHandler
    public void dropEvent(@NotNull final PlayerDropItemEvent ev) {
        if (ev.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        ev.getItemDrop().getItemStack().setAmount(0);
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
    public void teleportEvent(@NotNull final PlayerTeleportEvent ev) {
        final PlayerTeleportEvent.TeleportCause cause = ev.getCause();

        if (cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL
                && cause != PlayerTeleportEvent.TeleportCause.PLUGIN
                && cause != PlayerTeleportEvent.TeleportCause.COMMAND)
            ev.setCancelled(true);
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
    public void itemSpawnEvent(@NotNull final ItemSpawnEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void playerPickupItemEvent(@NotNull final PlayerAttemptPickupItemEvent ev) {
        ev.setCancelled(true);
        ev.getPlayer().getInventory().addItem(ev.getItem().getItemStack().clone());
        ev.getItem().setPickupDelay(40);
    }

    @EventHandler
    public void blockPhysicsEvent(@NotNull final BlockPhysicsEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockFromToEvent(@NotNull final BlockFromToEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockEvent(@NotNull final BlockSpreadEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockBurnEvent(@NotNull final BlockBurnEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockDestroyEvent(@NotNull final BlockDestroyEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockDispenseEvent(@NotNull final BlockDispenseEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockExplodeEvent(@NotNull final BlockExplodeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockFertilizeEvent(@NotNull final BlockFertilizeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockGrowEvent(@NotNull final BlockGrowEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockPistonEvent(@NotNull final BlockPistonExtendEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void blockPistonEvent(@NotNull final BlockPistonRetractEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void redstoneEvent(@NotNull final BlockRedstoneEvent ev) {
        ev.setNewCurrent(ev.getOldCurrent());
    }

}
