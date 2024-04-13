package org.crayne.archivist.index.cached;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {

    private MapUtil() {

    }

    @NotNull
    public static
    <K extends Comparable<? super K>, V> Map<K, V> sortMapByKey(@NotNull final Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new)
                );
    }

    @NotNull
    public static <K, V, N> List<Map.Entry<K, N>> mapKeys(@NotNull final Map<K, V> map,
                                                          @NotNull final Function<V, N> mapper) {
        return map.entrySet()
                .stream()
                .map(e -> Map.entry(e.getKey(), mapper.apply(e.getValue())))
                .toList();
    }

}
