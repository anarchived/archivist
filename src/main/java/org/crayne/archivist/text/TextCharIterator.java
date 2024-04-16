package org.crayne.archivist.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class TextCharIterator implements Iterator<Text> {

    private int index;
    private final int length;

    @NotNull
    private final Text iterating;

    @NotNull
    private Text parsed;

    @Nullable
    private Text previous;

    public TextCharIterator(@NotNull final Text iterating) {
        this.iterating = iterating;
        this.parsed = Text.empty();
        this.length = iterating.length();
        this.index = 0;
    }

    public boolean hasNext() {
        return index < length;
    }

    @NotNull
    public Text peek() {
        return iterating.charAt(index);
    }

    @NotNull
    public Text parsed() {
        return parsed;
    }

    public int currentWidth() {
        return parsed.pixelWidth();
    }

    public boolean currentlyBold() {
        return parsed.lastBold();
    }

    public void clearParsed() {
        clearParsed(Text.empty());
    }

    public void clearParsed(@NotNull final Text newText) {
        parsed = newText;
    }

    @Nullable
    public Text previous() {
        return previous;
    }

    public boolean previousWasWhitespace() {
        return previous != null && Character.isWhitespace(previous.singleChar());
    }

    @NotNull
    public Text next() {
        final Text next = peek();
        parsed = parsed.append(next);
        index++;
        return next;
    }
}
