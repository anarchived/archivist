package org.crayne.archivist.gui.book;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MinecraftFont {
    
    private MinecraftFont() {
        
    }

    public static int coloredIndex(@NotNull final String str, final int index) {
        final ColoredTextIterator it = new ColoredTextIterator(str);
        while (it.hasNext() && it.plainTextIndex() < index) it.next();

        return it.index();
    }

    public static int characterPixelWidth(final char c, final boolean bold) {
        return Optional.ofNullable(CHARACTER_WIDTH_MAP.get(c)).orElse(0) + (bold ? 1 : 0);
    }

    public static int characterPixelWidth(final char c) {
        return characterPixelWidth(c, false);
    }

    public static int stringPixelWidth(@NotNull final String str) {
        final ColoredTextIterator it = new ColoredTextIterator(str);
        while (it.hasNext()) it.next();

        return it.currentWidth();
    }

    public static int coloredStringLength(@NotNull final String str) {
        return stripColor(str).length();
    }

    @NotNull
    public static String coloredSubstring(@NotNull final String str, final int start, final int end) {
        if (!str.contains("§"))
            return str.substring(start, end);

        return str.substring(coloredIndex(str, start), coloredIndex(str, end));
    }

    public static char coloredCharAt(@NotNull final String str, final int index) {
        if (!str.contains("§")) return str.charAt(index);

        final int reindex = coloredIndex(str, index);
        return stripColor(str.substring(reindex)).charAt(0);
    }

    @NotNull
    public static String stripColor(@NotNull final String str) {
        return str.replaceAll("§.", "");
    }
    
    @NotNull
    private static final Map<Character, Integer> CHARACTER_WIDTH_MAP = new HashMap<>() {{
        put('0', 5);
        put('1', 5);
        put('2', 5);
        put('3', 5);
        put('4', 5);
        put('5', 5);
        put('6', 5);
        put('7', 5);
        put('8', 5);
        put('9', 5);

        put('A', 5);
        put('B', 5);
        put('C', 5);
        put('D', 5);
        put('E', 5);
        put('F', 5);
        put('G', 5);
        put('H', 5);
        put('I', 3);
        put('J', 5);
        put('K', 5);
        put('L', 5);
        put('M', 5);
        put('N', 5);
        put('O', 5);
        put('P', 5);
        put('Q', 5);
        put('R', 5);
        put('S', 5);
        put('T', 5);
        put('U', 5);
        put('V', 5);
        put('W', 5);
        put('X', 5);
        put('Y', 5);
        put('Z', 5);

        put('a', 5);
        put('b', 5);
        put('c', 5);
        put('d', 5);
        put('e', 5);
        put('f', 4);
        put('g', 5);
        put('h', 5);
        put('i', 1);
        put('j', 5);
        put('k', 4);
        put('l', 2);
        put('m', 5);
        put('n', 5);
        put('o', 5);
        put('p', 5);
        put('q', 5);
        put('r', 5);
        put('s', 5);
        put('t', 3);
        put('u', 5);
        put('v', 5);
        put('w', 5);
        put('x', 5);
        put('y', 5);
        put('z', 5);

        put('<', 4);
        put('{', 3);
        put('[', 3);
        put('(', 3);

        put('>', 4);
        put('}', 3);
        put(']', 3);
        put(')', 3);

        put('*', 3);
        put('\\', 5);
        put(':', 1);
        put(',', 1);
        put('"', 3);
        put('!', 1);
        put('#', 5);
        put('%', 5);
        put('.', 1);
        put('?', 5);
        put(';', 1);
        put('\'', 1);
        put('/', 5);
        put(' ', 4);
    }};

}
