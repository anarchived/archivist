package org.crayne.archivist.gui.markdown;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        return String.join("", decoration);
    }

    @NotNull
    public static String formatLine(@NotNull final String line, @NotNull final String decoration) {
        return decoration
                + line.substring(countPoundSymbols(line)).trim();
    }

    public static final int
            MAX_LINES_PER_PAGE = 14,
            MAX_CHARS_PER_LINE = 20,
            MAX_CHARS_PER_PAGE = 256,
            MAX_PAGES = 50;

    @NotNull
    public static String @NotNull [] renderMarkDownToBookContent(@NotNull final String markdownContent) {
        final List<String> result = new ArrayList<>();
        StringBuilder currentPage = new StringBuilder();
        int currentPageLength = 0;
        int lines = 0;

        for (final String line : markdownContent.split("\n")) {
            final String decoration = decorateLineMarkdown(line.trim());
            for (String lineSplit : line.split("(?<=\\G.{" + MAX_CHARS_PER_LINE + "})")) {
                final String lineSplitFormatted = ChatColor.RESET + formatLine(lineSplit.trim(), decoration);

                final boolean shouldSplit = lines + 1 >= MAX_LINES_PER_PAGE
                        || currentPageLength + lineSplit.length() > MAX_CHARS_PER_PAGE;

                if (shouldSplit && result.size() >= MAX_PAGES)
                    throw new UnsupportedOperationException("Markdown text is too large; " +
                            "Page reached 50 pages, cannot add another line");

                if (shouldSplit) {
                    result.add(currentPage.toString().trim());
                    currentPage = new StringBuilder(lineSplitFormatted + "\n");
                    currentPageLength = lineSplit.length();
                    lines = 0;
                    continue;
                }
                currentPage.append(lineSplitFormatted).append("\n");
                currentPageLength += lineSplit.length();
                lines++;
            }
        }
        if (!currentPage.isEmpty()) result.add(currentPage.toString());
        return result.toArray(new String[0]);
    }

    @NotNull
    public static ItemStack renderMarkdownToBook(@NotNull final String markdownText) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();
        meta.addPage(renderMarkDownToBookContent(markdownText));
        book.setItemMeta(meta);
        return book;
    }

    public static void displayMarkdownToPlayer(@NotNull final Player p, @NotNull final String markdownText) {
        openBook(p, renderMarkdownToBook(markdownText));
    }

}
