package org.crayne.archivist.index;

import org.jetbrains.annotations.NotNull;

public class IndexingException extends RuntimeException {

    public IndexingException(@NotNull final String s) {
        super(s);
    }

    public IndexingException(@NotNull final String s, @NotNull final Throwable t) {
        super(s, t);
    }

    public IndexingException() {
        super();
    }

    public IndexingException(@NotNull final Throwable t) {
        super(t);
    }

}
