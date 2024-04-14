package org.crayne.archivist.gui.book;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BookTextWrapper {

    private static final int
            MAX_PIXELS_PER_LINE = 115,
            MAX_LINES_PER_PAGE = 14,
            MAX_CHARS_PER_PAGE = 256,
            MAX_PAGES = 50;

    private BookTextWrapper() {

    }

    private static void verifyPageCount(final int count) {
        if (count > MAX_PAGES)
            throw new IllegalArgumentException("Could not wrap text for book; " +
                                               "Text exceeds max page amount of 50");
    }

    @NotNull
    public static String @NotNull [] wrapTextForBook(@NotNull final String text) {
        final List<String> pages = new ArrayList<>();
        final List<String> lines = wrapTextForPage(text);
        StringBuilder currentPage = new StringBuilder();
        int currentLineCount = 0;

        for (final String lineRaw : lines) {
            for (final String lineChunk : lineRaw.split("(?<=\\G.{" + MAX_CHARS_PER_PAGE + "})")) {
                for (final String line : lineChunk.split("\n")) {
                    final boolean exceedsMaxChars = currentPage.length() + line.length() > MAX_CHARS_PER_PAGE;
                    final boolean exceedsMaxLines = currentLineCount + 1 > MAX_LINES_PER_PAGE;

                    if (exceedsMaxLines || exceedsMaxChars) {
                        verifyPageCount(pages.size());
                        pages.add(currentPage.toString().trim());
                        currentPage = new StringBuilder();
                        currentLineCount = 0;
                    }
                    final boolean pageHasNoContent = MinecraftFont.stripColor(currentPage.toString()).isBlank();
                    final boolean nextLineAddsNoContent = MinecraftFont.stripColor(line).isBlank();

                    if (pageHasNoContent && nextLineAddsNoContent)
                        continue;

                    currentPage.append(line).append("\n");
                    currentLineCount++;
                }
            }
        }
        if (!currentPage.toString().isBlank()) {
            verifyPageCount(pages.size());
            pages.add(currentPage.toString().trim());
        }
        return pages.toArray(new String[0]);
    }

    @NotNull
    public static List<String> wrapTextForPage(@NotNull final String text) {
        return wrapTextByWidth(text, MAX_PIXELS_PER_LINE);
    }

    @NotNull
    private static final Pattern URL_REGEX = Pattern.compile("(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    @NotNull
    private static String wrap(@NotNull final ColoredTextIterator it, final int nextCharacter) {
        String resultLine = it.parsed();

        // dont split urls, make them easily clickable
        final Matcher matcher = URL_REGEX.matcher(resultLine);
        if (matcher.find()) {
            final StringBuilder fullURL = new StringBuilder(resultLine);
            while (it.hasNext() && URL_REGEX.matcher(fullURL.toString()).find()) {
                fullURL.append(it.next());
            }
            return "";
        }

        final int length = MinecraftFont.coloredStringLength(resultLine);
        String nextBegin = "";

        final boolean splitsWordInHalf = length != 0
                                         && !Character.isWhitespace(nextCharacter)
                                         && !Character.isWhitespace(it.previous());

        if (splitsWordInHalf) {
            for (int j = 1; j < length; j++) {
                final int backwardsCh = MinecraftFont.coloredCharAt(resultLine, length - j);
                if (!Character.isWhitespace(backwardsCh)) continue;

                nextBegin  = MinecraftFont.coloredSubstring(resultLine, length - j + 1, length);
                resultLine = MinecraftFont.coloredSubstring(resultLine, 0, length - j);
                break;
            }
        }
        it.clearParsed(nextBegin);
        return resultLine.trim();
    }

    @NotNull
    public static List<String> wrapTextByWidth(@NotNull final String text, final int maxWidth) {
        final List<String> result = new ArrayList<>();
        final ColoredTextIterator it = new ColoredTextIterator(text);

        while (it.hasNext()) {
            final char ch = it.peek();
            if (ch == '\n') {
                result.add(it.parsed().trim());
                it.clearParsed();
                it.next();
                continue;
            }
            final int charWidth = MinecraftFont.characterPixelWidth(ch, it.bold());
            final boolean shouldWrap = it.currentWidth() + charWidth + 1 >= maxWidth;

            if (shouldWrap) {
                final String wrapped = wrap(it, ch);
                if (!wrapped.isEmpty()) result.add(wrapped);
            }
            if (it.hasNext())
                it.next();
        }
        final String remaining = it.parsed();
        if (!remaining.isBlank())
            result.add(remaining.trim());

        return result;
    }

}
