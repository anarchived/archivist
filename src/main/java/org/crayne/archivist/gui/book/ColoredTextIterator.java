package org.crayne.archivist.gui.book;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ColoredTextIterator implements Iterator<Character> {

    private int plainTextIndex, index, currentWidth;
    private boolean bold, expectsColorCode;
    private char previousCharacter;

    @NotNull
    private final String coloredText;

    @NotNull
    private StringBuilder parsed;

    public ColoredTextIterator(@NotNull final String coloredText) {
        this.coloredText = coloredText;
        this.parsed = new StringBuilder();
    }

    public boolean hasNext() {
        final boolean baseCase = index < coloredText.length();
        if (baseCase && coloredText.charAt(index) == '§')
            return index + 2 < coloredText.length();

        return baseCase;
    }

    public char previous() {
        return previousCharacter;
    }

    public void clearParsed() {
        clearParsed("");
    }

    public void clearParsed(@NotNull final String init) {
        parsed = new StringBuilder(init);
        currentWidth = MinecraftFont.stringPixelWidth(init);
    }

    @NotNull
    public String parsed() {
        return parsed.toString();
    }

    public char peek(final int forwardFeed, final boolean expectsColorCode) {
        final char ch = coloredText.charAt(forwardFeed + index);

        if (ch == '§')
            return peek(1, true);

        if (expectsColorCode)
            return peek(1, false);

        return ch;
    }

    public char peek() {
        return peek(0, expectsColorCode);
    }

    @NotNull
    public Character next() {
        final char ch = coloredText.charAt(index);
        parsed.append(ch);

        if (ch == '§') {
            expectsColorCode = true;
            index++;
            return next();
        }
        if (expectsColorCode) {
            switch (ch) {
                case 'l' -> bold = true;
                case 'r' -> bold = false;
            }
            expectsColorCode = false;
            index++;
            return next();
        }
        index++;
        plainTextIndex++;
        currentWidth += MinecraftFont.characterPixelWidth(ch) + (bold ? 1 : 0) + 1;
        previousCharacter = ch;
        return ch;
    }

    public int currentWidth() {
        return currentWidth;
    }

    public int index() {
        return index;
    }

    public boolean bold() {
        return bold;
    }

    public int plainTextIndex() {
        return plainTextIndex;
    }
}
