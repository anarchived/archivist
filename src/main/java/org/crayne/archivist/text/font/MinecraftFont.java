package org.crayne.archivist.text.font;

import java.util.Optional;

public class MinecraftFont {
    
    private MinecraftFont() {
        
    }

    public static int characterPixelWidth(final char c, final boolean bold) {
        return Optional.ofNullable(org.bukkit.map.MinecraftFont.Font.getChar(c))
                .map(i -> i.getWidth() + (bold ? 1 : 0))
                .orElse(-1);
    }

    public static int characterPixelWidth(final char c) {
        return characterPixelWidth(c, false);
    }

}
