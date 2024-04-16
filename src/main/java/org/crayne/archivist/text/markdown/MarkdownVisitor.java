package org.crayne.archivist.text.markdown;

import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.commonmark.node.*;
import org.crayne.archivist.text.ChatText;
import org.crayne.archivist.text.formatting.Formatting;
import org.crayne.archivist.text.formatting.coloring.Coloring;
import org.crayne.archivist.text.formatting.styling.StylingVariant;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MarkdownVisitor extends AbstractVisitor {

    @NotNull
    private ChatText result;

    public MarkdownVisitor() {
        this.result = ChatText.empty();
    }

    @NotNull
    private static Formatting formatHeader(final int level) {
        return switch (level) {
            case 1 -> Formatting.styled(StylingVariant.BOLD);
            case 2 -> Formatting.styled(StylingVariant.ITALIC, StylingVariant.BOLD);
            case 3 -> Formatting.styled(StylingVariant.ITALIC);
            default -> Formatting.none();
        };
    }

    public void append(@NotNull final ChatText next) {
        result = result.append(next);
    }

    public void lineBreak(final int amount) {
        append(ChatText.text("\n".repeat(amount)));
    }

    public void lineBreak() {
        lineBreak(1);
    }

    public void visit(final Header header) {
        final Formatting headerFormatting = formatHeader(header.getLevel());

        if (!result.isBlank())
            lineBreak(2);

        append(ChatText.text(ChatText.Part.part("", headerFormatting)));
        visitChildren(header);
        lineBreak();
    }

    public void visit(final SoftLineBreak softLineBreak) {
        lineBreak();
    }

    public void visit(final HardLineBreak hardLineBreak) {
        lineBreak();
    }

    public void visit(final Text text) {
        append(ChatText.text(text.getLiteral()));
    }

    public void visit(final Emphasis emphasis) {
        append(ChatText.text("§o"));
        visitChildren(emphasis);
        append(ChatText.text("§r"));
    }

    public void visit(final StrongEmphasis emphasis) {
        append(ChatText.text("§l"));
        visitChildren(emphasis);
        append(ChatText.text("§r"));
    }

    @NotNull
    private static final Formatting
            LINK_FORMATTING = Formatting.formatted(Coloring.rgb(85, 151, 237), StylingVariant.UNDERLINED);

    public void visit(final Link link) {
        final String url = link.getDestination();
        final String linkText = Optional.ofNullable(link.getTitle()).orElse(url);
        final String infoText = "Click to open " + url;

        final ChatText textWhenHovered = ChatText.text(infoText).prependFormatting(LINK_FORMATTING);
        final ClickEvent clickEvent = ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url);
        final HoverEvent<?> hoverEvent = HoverEvent.showText(textWhenHovered.component());
        final ChatText clickableText = ChatText.clickableText(linkText, clickEvent).prependFormatting(LINK_FORMATTING);

        append(clickableText.hoverable(hoverEvent));
    }

    @NotNull
    public ChatText result() {
        return result;
    }

}
