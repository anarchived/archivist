package org.crayne.archivist.text.formatting.coloring;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class NamedColoring {
    
    private NamedColoring() {
        
    }

    @NotNull
    public static final Coloring BLACK = Coloring.rgb(0);
    @NotNull
    public static final Coloring DARK_BLUE = Coloring.rgb(170);
    @NotNull
    public static final Coloring DARK_GREEN = Coloring.rgb(43520);
    @NotNull
    public static final Coloring DARK_AQUA = Coloring.rgb(43690);
    @NotNull
    public static final Coloring DARK_RED = Coloring.rgb(11141120);
    @NotNull
    public static final Coloring DARK_PURPLE = Coloring.rgb(11141290);
    @NotNull
    public static final Coloring GOLD = Coloring.rgb(16755200);
    @NotNull
    public static final Coloring GRAY = Coloring.rgb(11184810);
    @NotNull
    public static final Coloring DARK_GRAY = Coloring.rgb(5592405);
    @NotNull
    public static final Coloring BLUE = Coloring.rgb(5592575);
    @NotNull
    public static final Coloring GREEN = Coloring.rgb(5635925);
    @NotNull
    public static final Coloring AQUA = Coloring.rgb(5636095);
    @NotNull
    public static final Coloring RED = Coloring.rgb(16733525);
    @NotNull
    public static final Coloring LIGHT_PURPLE = Coloring.rgb(16733695);
    @NotNull
    public static final Coloring YELLOW = Coloring.rgb(16777045);
    @NotNull
    public static final Coloring WHITE = Coloring.rgb(16777215);

    @NotNull
    public static final Map<Coloring, String> NAMED_COLORS = Map.ofEntries(
            Map.entry(BLACK, "BLACK"),
            Map.entry(DARK_BLUE, "DARK_BLUE"),
            Map.entry(DARK_GREEN, "DARK_GREEN"),
            Map.entry(DARK_AQUA, "DARK_AQUA"),
            Map.entry(DARK_RED, "DARK_RED"),
            Map.entry(DARK_PURPLE, "DARK_PURPLE"),
            Map.entry(GOLD, "GOLD"),
            Map.entry(GRAY, "GRAY"),
            Map.entry(DARK_GRAY, "DARK_GRAY"),
            Map.entry(BLUE, "BLUE"),
            Map.entry(GREEN, "GREEN"),
            Map.entry(AQUA, "AQUA"),
            Map.entry(RED, "RED"),
            Map.entry(LIGHT_PURPLE, "LIGHT_PURPLE"),
            Map.entry(YELLOW, "YELLOW"),
            Map.entry(WHITE, "WHITE")
    );

    @NotNull
    public static final Map<Coloring, Character> NAMED_COLORS_CODES = Map.ofEntries(
            Map.entry(BLACK, '0'),
            Map.entry(DARK_BLUE, '1'),
            Map.entry(DARK_GREEN, '2'),
            Map.entry(DARK_AQUA, '3'),
            Map.entry(DARK_RED, '4'),
            Map.entry(DARK_PURPLE, '5'),
            Map.entry(GOLD, '6'),
            Map.entry(GRAY, '7'),
            Map.entry(DARK_GRAY, '8'),
            Map.entry(BLUE, '9'),
            Map.entry(GREEN, 'a'),
            Map.entry(AQUA, 'b'),
            Map.entry(RED, 'c'),
            Map.entry(LIGHT_PURPLE, 'd'),
            Map.entry(YELLOW, 'e'),
            Map.entry(WHITE, 'f')
    );


    
}
