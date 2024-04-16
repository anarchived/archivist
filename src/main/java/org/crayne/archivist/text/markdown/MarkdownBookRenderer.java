package org.crayne.archivist.text.markdown;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.crayne.archivist.text.ChatText;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MarkdownBookRenderer {

    private MarkdownBookRenderer() {}

    @NotNull
    public static ChatText renderMarkdown(@NotNull final String markdownText) {
        final Parser markdownParser = Parser.builder().build();
        final Node document = markdownParser.parse(markdownText);
        final MarkdownVisitor markdownVisitor = new MarkdownVisitor();
        document.accept(markdownVisitor);

        return markdownVisitor.result();
    }

    @NotNull
    public static Component @NotNull [] renderMarkdownToBookContent(@NotNull final String markdownText) {
        return Arrays.stream(renderMarkdown(markdownText).wrapBookPages())
                .map(ChatText::component)
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
