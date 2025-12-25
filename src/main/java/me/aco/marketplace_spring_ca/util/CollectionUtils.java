package me.aco.marketplace_spring_ca.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CollectionUtils {
    public static <T> Stream<T> streamOf(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
