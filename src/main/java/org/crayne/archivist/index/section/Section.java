package org.crayne.archivist.index.section;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Section {

    @NotNull
    private String text;

    @NotNull
    private final String title;

    public Section(@NotNull final String title, @NotNull final String text) {
        this.title = title;
        this.text = text;
    }

    public Section(@NotNull final String title) {
        this(title, "");
    }

    @NotNull
    public String title() {
        return title;
    }

    @NotNull
    public String text() {
        return text;
    }

    public void append(@NotNull final String text) {
        this.text += text;
    }

    @NotNull
    public List<String> lines() {
        return Arrays.stream(text.split("\n")).toList();
    }

    @NotNull
    public List<String> enumeration() {
        return lines().stream().map(Section::sanitize).toList();
    }

    @NotNull
    public String toString() {
        return title + "\n" + text;
    }

    @NotNull
    public Map<String, String> map() {
        return lines().stream()
                .filter(s -> s.contains(":"))
                .map(Section::splitEnumerationEntry)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public Optional<String> mapped(@NotNull final String key) {
        return Optional.ofNullable(map().get(key));
    }

    @NotNull
    private static String sanitize(@NotNull final String key) {
        return key.startsWith("-") ? key.substring(1).trim() : key.trim();
    }

    @NotNull
    private static Map.Entry<String, String> splitEnumerationEntry(@NotNull final String line) {
        final String key = StringUtils.substringBefore(line, ":");
        final String value = StringUtils.substringAfter(line, ":");

        return Map.entry(sanitize(key), value.trim());
    }
}
