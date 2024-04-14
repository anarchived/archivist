package org.crayne.archivist.gui.markdown;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.crayne.archivist.gui.book.BookTextWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MarkdownBookRenderer {

    private MarkdownBookRenderer() {}

    public static void openBook(@NotNull final Player p, @NotNull final ItemStack book) {
        final int slot = p.getInventory().getHeldItemSlot();
        final ItemStack old = p.getInventory().getItem(slot);
        p.getInventory().setItem(slot, book);

        final ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        final PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
        p.closeInventory();
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        p.getInventory().setItem(slot, old);
    }

    public static int countPoundSymbols(@NotNull final String line) {
        if (!line.startsWith("#")) return 0;
        int count = 1;

        //noinspection StatementWithEmptyBody
        for (; count < line.length() && line.charAt(count) == '#'; count++) ;

        return count;
    }

    @NotNull
    public static String decorateLineMarkdown(@NotNull final String line) {
        final int count = countPoundSymbols(line);
        final String decoration = switch (count) {
            case 0 -> "";
            case 1 -> ChatColor.BOLD.toString();
            case 2 -> ChatColor.ITALIC + ChatColor.BOLD.toString();
            default -> ChatColor.ITALIC.toString();
        };
        return String.join("", decoration)
               + line.substring(count).trim();
    }

    @NotNull
    public static String renderMarkdown(@NotNull final String markdownText) {
        return Arrays.stream(markdownText.split("\n"))
                .map(MarkdownBookRenderer::decorateLineMarkdown)
                .map(StringEscapeUtils::unescapeJava)
                .map(s -> s.replace("\\", ""))
                .collect(Collectors.joining("\n" + ChatColor.RESET));
    }

    @NotNull
    public static String @NotNull [] renderMarkdownToBookContent(@NotNull final String markdownText) {
        return BookTextWrapper.wrapTextForBook(renderMarkdown(markdownText));
    }

    @NotNull
    public static ItemStack renderMarkdownToBook(@NotNull final String markdownText) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.addPage(renderMarkdownToBookContent(markdownText));
        book.setItemMeta(meta);
        return book;
    }

    public static void displayMarkdownToPlayer(@NotNull final Player p, @NotNull final String markdownText) {
        openBook(p, renderMarkdownToBook(markdownText));
    }

}
