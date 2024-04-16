package org.crayne.archivist.text.markdown;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.crayne.archivist.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MarkdownBookRenderer {

    private MarkdownBookRenderer() {}

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
            case 1 -> "§l";
            case 2 -> "§o§l";
            default -> "§o";
        };
        return String.join("", decoration)
               + line.substring(count).trim();
    }

    @NotNull
    public static Text renderMarkdown(@NotNull final String markdownText) {
        return Text.text(Arrays.stream(markdownText.split("\n"))
                .map(MarkdownBookRenderer::decorateLineMarkdown)
                .map(StringEscapeUtils::unescapeJava)
                .map(s -> s.replace("\\", ""))
                .collect(Collectors.joining("\n§r")));
    }

    @NotNull
    public static Component @NotNull [] renderMarkdownToBookContent(@NotNull final String markdownText) {
        return Arrays.stream(renderMarkdown(markdownText).wrapBookPages())
                .map(Text::component)
                .toList()
                .toArray(new Component[0]);
    }

    @NotNull
    public static ItemStack renderMarkdownToBook(@NotNull final String markdownText) {
        final ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        final BookMeta meta = (BookMeta) book.getItemMeta();

        meta.setAuthor("Archivist");
        meta.setTitle("Archivist Book");
        meta.addPages(renderMarkdownToBookContent(markdownText));

        book.setItemMeta(meta);
        return book;
    }

    public static void displayMarkdownToPlayer(@NotNull final Player p, @NotNull final String markdownText) {
        p.openBook(renderMarkdownToBook(markdownText));
    }

}
