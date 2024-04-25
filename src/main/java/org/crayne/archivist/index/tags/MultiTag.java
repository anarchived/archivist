package org.crayne.archivist.index.tags;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class MultiTag {

    @NotNull
    private final UnaryTag type;

    @Nullable
    private final String additionalInformation;

    public MultiTag(@NotNull final UnaryTag type, @Nullable final String additionalInformation) {
        this.type = type;
        this.additionalInformation = additionalInformation;
    }

    @NotNull
    public UnaryTag type() {
        return type;
    }

    @NotNull
    public Optional<String> additionalInformation() {
        return Optional.ofNullable(additionalInformation);
    }

    @NotNull
    public String toString() {
        return type + additionalInformation().map(s -> ": " + s).orElse("");
    }

    public boolean hidden() {
        return type.hidden();
    }

    public boolean visible() {
        return !hidden();
    }

    @NotNull
    public static ItemStack createIconItemStack(@NotNull final Collection<MultiTag> tags) {
        Material type = Material.BOOK;
        boolean glint = false;

        for (final MultiTag tag : tags) {
            switch (tag.type) {
                case ICON -> {
                    if (tag.additionalInformation == null) continue;
                    final Material newType = Material.matchMaterial(tag.additionalInformation);
                    if (newType != null) type = newType;
                }
                case GLINT -> glint = true;
            }
        }
        final ItemStack itemStack = new ItemStack(type);
        if (!glint) return itemStack;

        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, false);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @NotNull
    public static Optional<MultiTag> parse(@NotNull final String text) {
        if (!text.contains(":"))
            return UnaryTag.parse(text)
                    .map(tag -> new MultiTag(tag, null));

        final String type = StringUtils.substringBefore(text, ":").trim();
        final String info = StringUtils.substringAfter(text, ":").trim();

        return UnaryTag.parse(type)
                .map(tag -> new MultiTag(tag, info));
    }

}
