package org.crayne.archivist.command.query;

import org.crayne.archivist.index.cache.SaveCache;
import org.crayne.archivist.index.cache.ServerCache;
import org.crayne.archivist.index.tags.MultiTag;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class SearchQuery {

    @NotNull
    private final Set<ServerCache> servers;

    @NotNull
    private final String query;

    @NotNull
    private final List<String> sanitizedQuery;

    @NotNull
    private final Set<MultiTag> querySearchTags;

    public SearchQuery(@NotNull final Set<ServerCache> servers,
                       @NotNull final String query,
                       @NotNull final Set<MultiTag> querySearchTags) {
        this.servers = servers;
        this.query = query;
        this.querySearchTags = querySearchTags;
        this.sanitizedQuery = sanitizeQuerySplit(query);
    }

    @NotNull
    public static final String EMPTY_QUERY = "";

    @NotNull
    public static SearchQuery empty() {
        return new SearchQuery(Collections.emptySet(), EMPTY_QUERY, Collections.emptySet());
    }

    @NotNull
    public static SearchQuery parse(@NotNull final String @NotNull [] args) {
        return parse(Collections.emptySet(), args);
    }

    @NotNull
    public static SearchQuery parse(@NotNull final Set<ServerCache> servers,
                                    @NotNull final String @NotNull [] args) {
        final List<String> query = new ArrayList<>(Arrays.stream(args).toList());

        final List<String> tags = query.stream()
                .filter(s -> s.startsWith("-"))
                .map(s -> s.substring(1).trim())
                .toList();

        query.removeIf(s -> s.startsWith("-"));

        final Set<MultiTag> tagsMapped = tags.stream()
                .map(MultiTag::parse)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        return new SearchQuery(servers, String.join(" ", query), tagsMapped);
    }

    @NotNull
    public String createTitle() {
        final String color = "§1§l";
        final String lookupMode = query.isEmpty() ? "Bases of" : "Searching";
        if (!servers.isEmpty())
            return color + lookupMode + " " + serversToString();

        return color + lookupMode + " Archive";
    }

    @NotNull
    public String serversToString() {
        return servers.stream()
                .map(ServerCache::name)
                .collect(Collectors.joining(", "));
    }

    private static boolean matches(@NotNull final Set<MultiTag> tags, @NotNull final MultiTag tag) {
        for (final MultiTag expected : tags) {
            if (expected.type() != tag.type()) continue;

            final Optional<String> expectedInfo = expected.additionalInformation();
            final Optional<String> givenInfo = tag.additionalInformation();
            if (expectedInfo.isEmpty() || givenInfo.isEmpty()) return true;

            return sanitizeQuery(givenInfo.get()).contains(expectedInfo.get());
        }
        return false;
    }

    public int matches(@NotNull final SaveCache save) {
        final List<String> nameSanitized = new ArrayList<>(sanitizeQuerySplit(save.name()));
        nameSanitized.retainAll(sanitizedQuery);

        if (!sanitizedQuery.isEmpty() && nameSanitized.isEmpty()) return 0;
        final boolean tagsMatch = querySearchTags.isEmpty()
                || save.tags().stream().anyMatch(tag -> matches(querySearchTags, tag));

        return tagsMatch ? nameSanitized.size() : 0;
    }

    @NotNull
    private static String sanitizeQuery(@NotNull final String query) {
        return query.replaceAll("[_']", "").toLowerCase();
    }

    @NotNull
    private static List<String> sanitizeQuerySplit(@NotNull final String query) {
        return Arrays.stream(sanitizeQuery(query).split(" ")).toList();
    }

    @NotNull
    public static SearchQuery query(@NotNull final Set<ServerCache> servers, @NotNull final String query) {
        return new SearchQuery(servers, query, Collections.emptySet());
    }

    @NotNull
    public static SearchQuery query(@NotNull final String query) {
        return new SearchQuery(Collections.emptySet(), query, Collections.emptySet());
    }

    @NotNull
    public static SearchQuery query(@NotNull final Set<ServerCache> servers) {
        return query(servers, EMPTY_QUERY);
    }

    @NotNull
    public static SearchQuery query(@NotNull final Collection<MultiTag> querySearchTags) {
        return new SearchQuery(Collections.emptySet(), EMPTY_QUERY, new HashSet<>(querySearchTags));
    }

    @NotNull
    public static SearchQuery query(@NotNull final String query,
                                    @NotNull final Collection<MultiTag> querySearchTags) {
        return new SearchQuery(Collections.emptySet(), query, new HashSet<>(querySearchTags));
    }

    @NotNull
    public static SearchQuery query(@NotNull final Set<ServerCache> servers,
                                    @NotNull final String query,
                                    @NotNull final Set<MultiTag> querySearchTags) {
        return new SearchQuery(servers, query, new HashSet<>(querySearchTags));
    }

    @NotNull
    public String query() {
        return query;
    }

    @NotNull
    public List<String> sanitizedQuery() {
        return sanitizedQuery;
    }

    @NotNull
    public Set<MultiTag> querySearchTags() {
        return Collections.unmodifiableSet(querySearchTags);
    }

    @NotNull
    public Set<ServerCache> servers() {
        return Collections.unmodifiableSet(servers);
    }
}
