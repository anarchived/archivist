package org.crayne.archivist.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArchivistInventory implements Listener {

    @NotNull
    private static final ItemStack BROWSER_ITEMSTACK = new ItemStack(Material.COMPASS) {{
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Browser");
        meta.setLore(List.of(ChatColor.GRAY + "Right click to browse the archive"));
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setItemMeta(meta);
    }};

    @NotNull
    private static final ItemStack ENDER_PEARL_ITEMSTACK = new ItemStack(Material.ENDER_PEARL) {{
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Ender Pearl");
        setItemMeta(meta);
    }};

    @NotNull
    private static final ItemStack FIREWORKS_ITEMSTACK = new ItemStack(Material.FIREWORK) {{
        final FireworkMeta meta = (FireworkMeta) getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Fireworks");
        meta.setPower(5);
        setItemMeta(meta);
    }};

    @NotNull
    private static final ItemStack NIGHT_VISION_ITEMSTACK = new ItemStack(Material.POTION) {{
        final PotionMeta meta = (PotionMeta) getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Night Vision");
        meta.addCustomEffect(new PotionEffect(
                PotionEffectType.NIGHT_VISION,
                20 * 60 * 15,
                1,
                false,
                false
        ), false);
        meta.setColor(Color.BLUE);

        setItemMeta(meta);
    }};

    @NotNull
    private static final ItemStack MILK_BUCKET_ITEMSTACK = new ItemStack(Material.MILK_BUCKET) {{
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Milk Bucket");

        setItemMeta(meta);
    }};

    @NotNull
    private static final ItemStack ELYTRA_ITEMSTACK = new ItemStack(Material.ELYTRA) {{
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Elytra");
        meta.setUnbreakable(true);
        meta.addEnchant(Enchantment.MENDING, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        setItemMeta(meta);
    }};

    public static boolean isBrowserItem(@Nullable final ItemStack itemStack) {
        return BROWSER_ITEMSTACK.equals(itemStack);
    }

    public static void createArchivistInventory(@NotNull final Player p) {
        final PlayerInventory inventory = p.getInventory();
        inventory.clear();

        inventory.setChestplate(ELYTRA_ITEMSTACK);
        inventory.setItem(4, BROWSER_ITEMSTACK.clone());
        inventory.setItem(5, ENDER_PEARL_ITEMSTACK.clone());
        inventory.setItem(6, FIREWORKS_ITEMSTACK.clone());
        inventory.setItem(7, NIGHT_VISION_ITEMSTACK.clone());
        inventory.setItem(8, MILK_BUCKET_ITEMSTACK.clone());

        inventory.setHeldItemSlot(4);
    }

    @EventHandler
    public void joinEvent(@NotNull final PlayerJoinEvent ev) {
        createArchivistInventory(ev.getPlayer());
    }

    @EventHandler
    public void itemConsumeEvent(@NotNull final PlayerItemConsumeEvent ev) {
        ev.setReplacement(ev.getItem().clone());
    }

}
