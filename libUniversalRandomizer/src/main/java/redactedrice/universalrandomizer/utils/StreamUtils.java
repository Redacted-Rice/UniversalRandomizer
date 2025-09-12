package redactedrice.universalrandomizer.utils;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import redactedrice.universalrandomizer.userobjectapis.Getter;
import redactedrice.universalrandomizer.wrappers.RandomOrdering;

public class StreamUtils {
    private StreamUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Modifies & returns passed stream
    public static <T, R> Stream<R> field(Stream<T> stream, Getter<T, R> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.map(getter::get);
    }

    // Modifies & returns passed stream
    public static <T, R> Stream<R> fieldArray(Stream<T> stream, Getter<T, R[]> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.flatMap(o -> Arrays.stream(getter.get(o)));
    }

    // Modifies & returns passed stream
    public static <T, C extends Collection<R>, R> Stream<R> fieldCollection(Stream<T> stream,
            Getter<T, C> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.flatMap(obj -> getter.get(obj).stream());
    }

    // Modifies & returns passed stream
    public static <T, S extends Stream<R>, R> Stream<R> fieldStream(Stream<T> stream,
            Getter<T, S> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.flatMap(getter::get);
    }

    // Modifies & returns passed stream
    public static <T, M extends Map<R, ?>, R> Stream<R> fieldMapKeys(Stream<T> stream,
            Getter<T, M> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.flatMap(obj -> getter.get(obj).keySet().stream());
    }

    // Modifies & returns passed stream
    public static <T, M extends Map<?, R>, R> Stream<R> fieldMapValues(Stream<T> stream,
            Getter<T, M> getter) {
        if (getter == null || stream == null) {
            return Stream.empty();
        }
        return stream.flatMap(obj -> getter.get(obj).values().stream());
    }

    // Modifies & returns passed stream
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> castType(Stream<?> stream) {
        if (stream == null) {
            return Stream.empty();
        }
        return stream.map(o -> (T) o);
    }

    public static <T> Stream<T> castType(Stream<?> stream, Class<T> clazz) {
        if (stream == null) {
            return Stream.empty();
        }
        return stream.map(clazz::cast);
    }

    // Modifies & returns passed stream
    public static <T> Stream<T> shuffle(Stream<T> stream) {
        return shuffle(stream, null);
    }

    // Modifies & returns passed stream
    public static <T> Stream<T> shuffle(Stream<T> stream, long seed) {
        return shuffle(stream, new Random(seed));
    }

    // Modifies & returns passed stream
    public static <T> Stream<T> shuffle(Stream<T> stream, Random rand) {
        if (stream == null) {
            return Stream.empty();
        }
        final Random nonNull = rand != null ? rand : new Random();
        return stream.map(o -> RandomOrdering.create(o, nonNull.nextLong()))
                .sorted(RandomOrdering::sortBySortingValue).map(RandomOrdering::getObject);
    }

    // Consumes passed stream
    public static <R, T> Map<R, List<T>> group(Stream<T> stream, Getter<T, R> groupingFn) {
        if (stream == null || groupingFn == null) {
            return Collections.emptyMap();
        }
        return stream.collect(Collectors.groupingBy(groupingFn::get));
    }

    public static <R, T> SortedMap<R, List<T>> sortedGroup(Stream<T> stream,
            Getter<T, R> groupingFn) {
        return sortedGroup(stream, groupingFn, null);
    }

    public static <R, T> SortedMap<R, List<T>> sortedGroup(Stream<T> stream,
            Getter<T, R> groupingFn, Comparator<? super R> keyComparator) {
        if (stream == null || groupingFn == null) {
            return Collections.emptySortedMap();
        }
        return stream.collect(Collectors.groupingBy(groupingFn::get,
                () -> new TreeMap<>(keyComparator), Collectors.toList()));
    }
}
