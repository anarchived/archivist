package org.crayne.archivist.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.crayne.archivist.text.font.MinecraftFont;
import org.crayne.archivist.text.formatting.Formatting;
import org.crayne.archivist.text.formatting.coloring.Coloring;
import org.crayne.archivist.text.formatting.coloring.NamedColoring;
import org.crayne.archivist.text.formatting.styling.Styling;
import org.crayne.archivist.text.formatting.styling.StylingVariant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Text {

    public static final char SECTION = '§', AMPERSAND = '&';

    public record Part(@NotNull String text,
                       @NotNull Formatting formatting,
                       @Nullable ClickEvent clickEvent,
                       @Nullable HoverEvent<?> hoverEvent) {

        public boolean equalDisplay(@NotNull final Part other) {
            return formatting.equals(other.formatting)
                    && Objects.equals(clickEvent, other.clickEvent)
                    && Objects.equals(hoverEvent, other.hoverEvent);
        }

        @NotNull
        public TextComponent component() {
            final TextComponent plain = Component.text(text)
                    .clickEvent(clickEvent)
                    .hoverEvent(hoverEvent);

            if (formatting.styling().reset())
                return plain;

            final TextColor textColor = formatting.adventureTextColor();
            final TextDecoration[] textDecorations = formatting.adventureTextDecorations();
            return plain.style(Style.style(textColor, textDecorations));
        }

        @NotNull
        public Part formatted(@NotNull final Formatting formatting) {
            return new Part(text, formatting, clickEvent, hoverEvent);
        }

        @NotNull
        public Part colored(@NotNull final Coloring coloring) {
            return formatted(Formatting.colored(coloring));
        }

        @NotNull
        public Part styled(@NotNull final Styling styling) {
            return formatted(Formatting.styled(styling));
        }

        @NotNull
        public Part styled(@NotNull final StylingVariant @NotNull ... stylingVariants) {
            return formatted(Formatting.styled(stylingVariants));
        }

        @NotNull
        public Part derive(@NotNull final String newPlainText) {
            return new Part(newPlainText, formatting, clickEvent, hoverEvent);
        }

        @NotNull
        public Part clickable(@Nullable final ClickEvent clickEvent) {
            return new Part(text, formatting, clickEvent, hoverEvent);
        }

        @NotNull
        public Part hoverable(@Nullable final HoverEvent<?> hoverEvent) {
            return new Part(text, formatting, clickEvent, hoverEvent);
        }

    }

    @NotNull
    private final List<Part> parts;

    private Text(@NotNull final Collection<Part> parts) {
        this.parts = List.copyOf(parts);
    }

    @NotNull
    public static Text empty() {
        return Text.text();
    }

    @NotNull
    public static Text text(@NotNull final Collection<Part> parts) {
        return new Text(parts);
    }

    @NotNull
    public static Text text(@NotNull final Part @NotNull ... parts) {
        return Text.text(Arrays.stream(parts).toList());
    }

    @NotNull
    public static Text text(@NotNull final String coloredText) {
        return text(coloredText, SECTION, null, null);
    }

    @NotNull
    public static Text clickableText(@NotNull final String coloredText, @Nullable final ClickEvent clickEvent) {
        return text(coloredText, SECTION, clickEvent, null);
    }

    @NotNull
    public static Text hoverableText(@NotNull final String coloredText, @Nullable final HoverEvent<?> hoverEvent) {
        return text(coloredText, SECTION, null, hoverEvent);
    }

    @NotNull
    public static Text clickableHoverableText(@NotNull final String coloredText,
                                              @Nullable final ClickEvent clickEvent,
                                              @Nullable final HoverEvent<?> hoverEvent) {
        return text(coloredText, SECTION, clickEvent, hoverEvent);
    }

    @NotNull
    public static TextComponent legacy(@NotNull final String s) {
        return Text.text(s).component();
    }

    @NotNull
    public TextComponent component() {
        TextComponent result = Component.text("");
        for (final Part part : parts) {
            result = result.append(part.component());
        }
        return result;
    }

    @NotNull
    public String legacyText() {
        return legacyText(SECTION);
    }

    @NotNull
    public String legacyText(final char colorCodeChar) {
        final StringBuilder result = new StringBuilder();
        for (final Part part : parts) {
            result.append(part.formatting.legacyFormatting(colorCodeChar))
                    .append(part.text);
        }
        return result.toString();
    }

    @NotNull
    public Text apply(@NotNull final Function<Part, Part> partFunction) {
        return new Text(parts.stream().map(partFunction).toList());
    }

    @NotNull
    public Text hoverable(@NotNull final HoverEvent<?> hoverEvent) {
        return apply(p -> p.hoverable(hoverEvent));
    }

    @NotNull
    public Text clickable(@NotNull final ClickEvent clickEvent) {
        return apply(p -> p.clickable(clickEvent));
    }

    @NotNull
    public Text colored(@NotNull final Coloring coloring) {
        return apply(p -> p.colored(coloring));
    }

    @NotNull
    public Text styled(@NotNull final Styling styling) {
        return apply(p -> p.styled(styling));
    }

    @NotNull
    public Text formatted(@NotNull final Formatting formatting) {
        return apply(p -> p.formatted(formatting));
    }

    @NotNull
    public String plainText() {
        return parts.stream()
                .map(Part::text)
                .collect(Collectors.joining());
    }

    public int length() {
        return plainText().length();
    }

    @NotNull
    public TextCharIterator charIterator() {
        return new TextCharIterator(this);
    }

    @NotNull
    public Text charAt(final int index) {
        int i = 0;
        for (final Part part : parts) {
            final String partText = part.text;
            final int length = partText.length();

            if (i + length <= index) {
                i += length;
                continue;
            }
            final String character = String.valueOf(partText.charAt(index - i));
            final Part characterPart = new Part(character, part.formatting, part.clickEvent, part.hoverEvent);
            return new Text(Collections.singleton(characterPart));
        }
        throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + i);
    }

    public int indexOf(final char c) {
        final int length = length();
        if (length == 0) return -1;

        int i = 0;
        while (i < length && charAt(i).singleChar() != c) i++;

        return i >= length ? -1 : i;
    }

    public int lastIndexOf(final char c) {
        final int length = length();
        if (length == 0) return -1;

        int i = length - 1;
        while (i >= 0 && charAt(i).singleChar() != c) i--;

        return i;
    }

    @NotNull
    public Text trimFirst() {
        final int length = length();
        if (length == 0) return empty();

        int i = 0;
        while (i < length && Character.isWhitespace(charAt(i).singleChar())) i++;

        return substring(i);
    }

    @NotNull
    public Text trim() {
        final int length = length();
        if (length == 0) return empty();

        int i = 0;
        while (i < length && Character.isWhitespace(charAt(i).singleChar())) i++;

        final Text firstSubstring = substring(i);

        int j = length - i - 1;
        while (j >= 0 && Character.isWhitespace(firstSubstring.charAt(j).singleChar())) j--;

        return firstSubstring.substring(0, j + 1);
    }

    @NotNull
    public Text substring(final int beginIndex, final int endIndex) {
        final int length = length();
        if (beginIndex < 0 || beginIndex > length || endIndex > length)
            throw new StringIndexOutOfBoundsException("begin " + beginIndex + ", end " + endIndex + ", length " + length);

        Text result = empty();
        for (int i = beginIndex; i < endIndex; i++) {
            result = result.append(charAt(i));
        }
        return result;
    }

    @NotNull
    public Text substring(final int beginIndex) {
        return substring(beginIndex, length());
    }

    public boolean isEmpty() {
        return !hasContent();
    }

    public boolean isBlank() {
        return plainText().isBlank();
    }

    public boolean hasContent() {
        return !(parts.isEmpty() || (parts.size() == 1 && parts.get(0).text.isEmpty()));
    }

    @NotNull
    public List<Text> splitChunks(final int chunkLength) {
        if (chunkLength <= 0) return Collections.emptyList();

        final List<Text> result = new ArrayList<>();
        Text currentChunk = empty();

        final int length = length();
        for (int i = 0; i < length; i++) {
            final boolean split = i % chunkLength == 0 && i != 0;
            if (split) {
                result.add(currentChunk);
                currentChunk = empty();
            }
            currentChunk = currentChunk.append(charAt(i));
        }
        if (currentChunk.hasContent())
            result.add(currentChunk);

        return result;
    }

    public char singleChar() {
        // charAt guarantees to return exactly one part with always exactly one char
        // or else it throws an index out of bounds
        return parts.get(0).text.charAt(0);
    }

    @NotNull
    public List<Text> splitByDelimiter(final char delimiter) {
        final List<Text> result = new ArrayList<>();
        Text currentChunk = empty();

        final int length = length();
        for (int i = 0; i < length; i++) {
            final Text c = charAt(i);
            final char ch = c.singleChar();

            if (ch == delimiter) {
                result.add(currentChunk);
                currentChunk = empty();
                continue;
            }
            currentChunk = currentChunk.append(c);
        }
        if (currentChunk.hasContent())
            result.add(currentChunk);

        return result;
    }

    public boolean contains(@NotNull final String coloredText) {
        return legacyText().contains(coloredText);
    }

    public int pixelWidth() {
        int width = 0;
        for (final Part part : parts) {
            final boolean bold = part.formatting.styling().bold();
            for (final char c : part.text.toCharArray()) {
                final int charWidth = MinecraftFont.characterPixelWidth(c, bold);
                if (charWidth < 0)
                    throw new IllegalArgumentException("Cannot find width of unsupported character '" + c + "'");

                width += charWidth + 1;
            }
        }
        return width;
    }

    private static final int
            MAX_PIXELS_PER_LINE = 115,
            MAX_LINES_PER_PAGE = 14,
            MAX_CHARS_PER_PAGE = 256,
            MAX_PAGES = 50;

    private static void verifyPageCount(final int count) {
        if (count > MAX_PAGES)
            throw new IllegalArgumentException("Could not wrap text for book; " +
                    "Text exceeds max page amount of 50");
    }

    @NotNull
    public Text @NotNull [] wrapBookPages() {
        final List<Text> pages = new ArrayList<>();
        final List<Text> lines = wrapSingleBookPage();

        Text currentPage = Text.empty();
        int currentLineCount = 0;

        for (final Text lineRaw : lines) {
            for (final Text line : lineRaw.splitChunks(MAX_CHARS_PER_PAGE)) {
                final boolean exceedsMaxChars = currentPage.length() + line.length() > MAX_CHARS_PER_PAGE;
                final boolean exceedsMaxLines = currentLineCount + 1 > MAX_LINES_PER_PAGE;

                if (exceedsMaxLines || exceedsMaxChars) {
                    verifyPageCount(pages.size());
                    pages.add(currentPage);
                    currentPage = Text.empty();
                    currentLineCount = 0;
                }
                final boolean pageHasNoContent = currentPage.isBlank();
                final boolean nextLineAddsNoContent = line.isBlank();

                if (pageHasNoContent && nextLineAddsNoContent)
                    continue;

                currentPage = currentPage.append(line).append(Text.text("\n"));
                currentLineCount++;
            }
            if (lineRaw.isEmpty()) {
                currentPage = currentPage.append(Text.text("\n"));
                currentLineCount++;
            }
        }
        if (!currentPage.isBlank()) {
            verifyPageCount(pages.size());
            pages.add(currentPage);
        }
        return pages.toArray(new Text[0]);
    }

    @NotNull
    public List<Text> wrapSingleBookPage() {
        return wrapTextByWidth(MAX_PIXELS_PER_LINE);
    }

    @NotNull
    public List<Text> wrapTextByWidth(final int maxWidth) {
        final List<Text> result = new ArrayList<>();
        final TextCharIterator it = charIterator();

        while (it.hasNext()) {
            final Text c = it.peek();
            final char ch = c.singleChar();
            if (ch == '\n') {
                result.add(it.parsed().trim());
                it.clearParsed();
                it.next();
                continue;
            }
            final int charWidth = MinecraftFont.characterPixelWidth(ch, it.currentlyBold());
            final boolean shouldWrap = it.currentWidth() + charWidth + 1 >= maxWidth;

            if (shouldWrap) {
                final Text wrapped = wrap(it, c);
                if (!wrapped.isEmpty()) result.add(wrapped);
            }
            if (it.hasNext())
                it.next();
        }
        final Text remaining = it.parsed().trim();
        if (!remaining.isEmpty())
            result.add(remaining);

        return result;
    }

    @NotNull
    private static Text wrap(@NotNull final TextCharIterator it, final Text nextCharacter) {
        Text resultLine = it.parsed();

        final int length = resultLine.length();
        Text nextBegin = Text.empty();

        final boolean splitsWordInHalf = length != 0
                && !Character.isWhitespace(nextCharacter.singleChar())
                && !it.previousWasWhitespace();

        if (splitsWordInHalf) {
            for (int j = 1; j < length; j++) {
                final Text backwardsCh = resultLine.charAt(length - j);
                if (!Character.isWhitespace(backwardsCh.singleChar())) continue;

                nextBegin  = resultLine.substring(length - j + 1, length);
                resultLine = resultLine.substring(0, length - j);
                break;
            }
        }
        it.clearParsed(nextBegin);
        return resultLine.trim();
    }

    public boolean lastBold() {
        return !parts.isEmpty() && parts.get(parts.size() - 1).formatting.styling().bold();
    }

    @NotNull
    public static Text text(@NotNull final String coloredText, final char colorCodeChar,
                            @Nullable final ClickEvent clickEvent, @Nullable final HoverEvent<?> hoverEvent) {
        final List<Part> parts = new ArrayList<>();

        Formatting currentFormatting = Formatting.none();
        boolean expectColorCode = false;

        for (final PrimitiveIterator.OfInt it = coloredText.chars().iterator(); it.hasNext(); ) {
            final int c = it.next();
            if (c == colorCodeChar) {
                expectColorCode = true;
                continue;
            }
            if (expectColorCode) {
                currentFormatting = currentFormatting.merge(parseCodedFormatting(it, c, colorCodeChar));
                expectColorCode = false;
                continue;
            }
            final Part nextPart = new Part(String.valueOf((char) c), currentFormatting, clickEvent, hoverEvent);

            if (mergeWithPreviousPart(parts, nextPart))
                parts.add(nextPart);
        }
        return new Text(parts);
    }

    @NotNull
    public Text append(@NotNull final Text text) {
        final List<Part> parts = new ArrayList<>(this.parts);
        for (final Part nextPart : text.parts) {
            if (mergeWithPreviousPart(parts, nextPart))
                parts.add(nextPart);
        }
        return new Text(parts);
    }

    private static boolean mergeWithPreviousPart(@NotNull final List<Part> parts,
                                                 @NotNull final Part nextPart) {
        if (parts.isEmpty()) return true;

        final Part previousPart = parts.get(parts.size() - 1);
        if (previousPart.equalDisplay(nextPart)) {
            parts.remove(parts.size() - 1);
            parts.add(new Part(previousPart.text + nextPart.text,
                    previousPart.formatting,
                    previousPart.clickEvent,
                    previousPart.hoverEvent));
            return false;
        }
        return true;
    }

    @NotNull
    private static Formatting parseCodedFormatting(@NotNull final PrimitiveIterator.OfInt it,
                                                   final int color, final char colorCodeChar) {
        return switch (color) {
            case '0' -> Formatting.colored(NamedColoring.BLACK);
            case '1' -> Formatting.colored(NamedColoring.DARK_BLUE);
            case '2' -> Formatting.colored(NamedColoring.DARK_GREEN);
            case '3' -> Formatting.colored(NamedColoring.DARK_AQUA);
            case '4' -> Formatting.colored(NamedColoring.DARK_RED);
            case '5' -> Formatting.colored(NamedColoring.DARK_PURPLE);
            case '6' -> Formatting.colored(NamedColoring.GOLD);
            case '7' -> Formatting.colored(NamedColoring.GRAY);
            case '8' -> Formatting.colored(NamedColoring.DARK_GRAY);
            case '9' -> Formatting.colored(NamedColoring.BLUE);
            case 'a' -> Formatting.colored(NamedColoring.GREEN);
            case 'b' -> Formatting.colored(NamedColoring.AQUA);
            case 'c' -> Formatting.colored(NamedColoring.RED);
            case 'd' -> Formatting.colored(NamedColoring.LIGHT_PURPLE);
            case 'e' -> Formatting.colored(NamedColoring.YELLOW);
            case 'f' -> Formatting.colored(NamedColoring.WHITE);
            case 'l' -> Formatting.styled(StylingVariant.BOLD);
            case 'm' -> Formatting.styled(StylingVariant.STRIKETHROUGH);
            case 'n' -> Formatting.styled(StylingVariant.UNDERLINED);
            case 'o' -> Formatting.styled(StylingVariant.ITALIC);
            case 'k' -> Formatting.styled(StylingVariant.OBFUSCATED);
            case 'r' -> Formatting.styled(StylingVariant.RESET);
            case 'x' -> parseHexColorFormatting(it, colorCodeChar);
            default -> Formatting.none();
        };
    }

    @NotNull
    private static Formatting parseHexColorFormatting(@NotNull final PrimitiveIterator.OfInt it, final char colorCodeChar) {
        int i = 0;
        final StringBuilder hexDigits = new StringBuilder();
        while (it.hasNext() && i < 6) {
            final int nextCode = it.next();
            if (nextCode != colorCodeChar) return Formatting.none();

            final int next = it.next();
            hexDigits.append((char) next);
            i++;
        }

        try {
            final int hex = Integer.parseInt(hexDigits.toString(), 16);
            return Formatting.colored(Coloring.rgb(hex));
        } catch (final NumberFormatException e) {
            return Formatting.none();
        }
    }


}
