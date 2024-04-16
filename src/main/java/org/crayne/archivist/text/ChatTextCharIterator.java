package org.crayne.archivist.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class ChatTextCharIterator implements Iterator<ChatText> {

    private int index;
    private final int length;

    @NotNull
    private final ChatText iterating;

    @NotNull
    private ChatText parsed;

    @Nullable
    private ChatText previous;

    public ChatTextCharIterator(@NotNull final ChatText iterating) {
        this.iterating = iterating;
        this.parsed = ChatText.empty();
        this.length = iterating.length();
        this.index = 0;
    }

    public boolean hasNext() {
        return index < length;
    }

    @NotNull
    public ChatText peek() {
        return iterating.charAt(index);
    }

    @NotNull
    public ChatText parsed() {
        return parsed;
    }

    public int currentWidth() {
        return parsed.pixelWidth();
    }

    public boolean currentlyBold() {
        return parsed.lastBold();
    }

    public void clearParsed() {
        clearParsed(ChatText.empty());
    }

    public void clearParsed(@NotNull final ChatText newText) {
        parsed = newText;
    }

    @Nullable
    public ChatText previous() {
        return previous;
    }

    public boolean previousWasWhitespace() {
        return previous != null && Character.isWhitespace(previous.singleChar());
    }

    @NotNull
    public ChatText next() {
        final ChatText next = peek();
        parsed = parsed.append(next);
        index++;
        return next;
    }
}
