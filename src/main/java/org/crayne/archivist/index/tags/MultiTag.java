package org.crayne.archivist.index.tags;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MultiTag {

    @NotNull
    private final UnaryTag type;

    @Nullable
    private final String additionalInformation;

    public MultiTag(@NotNull final UnaryTag type, @Nullable final String additionalInformation) {
        this.type = type;
        this.additionalInformation = additionalInformation;
    }

    @NotNull
    public UnaryTag type() {
        return type;
    }

    @NotNull
    public Optional<String> additionalInformation() {
        return Optional.ofNullable(additionalInformation);
    }

    @NotNull
    public String toString() {
        return type + additionalInformation().map(s -> ": " + s).orElse("");
    }

    @NotNull
    public static Optional<MultiTag> parse(@NotNull final String text) {
        if (!text.contains(":"))
            return UnaryTag.parse(text)
                    .map(tag -> new MultiTag(tag, null));

        final String type = StringUtils.substringBefore(text, ":").trim();
        final String info = StringUtils.substringAfter(text, ":").trim();

        return UnaryTag.parse(type)
                .map(tag -> new MultiTag(tag, info));
    }

}
