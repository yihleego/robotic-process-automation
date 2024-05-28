package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leego Yih
 */
public class Option<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = -121222596239767866L;
    private final K key;
    private final V value;

    public Option(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public <X, Y> Option<X, Y> map(Function<? super K, ? extends X> keyMapper, Function<? super V, ? extends Y> valueMapper) {
        return new Option<>(keyMapper.apply(key), valueMapper.apply(value));
    }


    public static <K, V> Option<K, V> of(K key, V value) {
        return new Option<>(key, value);
    }

    public static <K, V> List<Option<K, V>> of(Map<K, V> map) {
        return of(map.entrySet().stream(), Map.Entry::getKey, Map.Entry::getValue, Collectors.toList());
    }

    public static <K, V, C extends Collection<Option<K, V>>> C of(Map<K, V> map, Supplier<C> collectionFactory) {
        return of(map.entrySet().stream(), Map.Entry::getKey, Map.Entry::getValue, Collectors.toCollection(collectionFactory));
    }

    public static <K, V, X, Y> List<Option<K, V>> of(Map<X, Y> map, Function<X, K> keyMapper, Function<Y, V> valueMapper) {
        return of(map.entrySet().stream(), e -> keyMapper.apply(e.getKey()), e -> valueMapper.apply(e.getValue()), Collectors.toList());
    }

    public static <K, V, X, Y, C extends Collection<Option<K, V>>> C of(Map<X, Y> map, Function<X, K> keyMapper, Function<Y, V> valueMapper, Supplier<C> collectionFactory) {
        return of(map.entrySet().stream(), e -> keyMapper.apply(e.getKey()), e -> valueMapper.apply(e.getValue()), Collectors.toCollection(collectionFactory));
    }

    public static <K, V, T> List<Option<K, V>> of(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return of(Arrays.stream(array), keyMapper, valueMapper, Collectors.toList());
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> C of(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        return of(Arrays.stream(array), keyMapper, valueMapper, Collectors.toCollection(collectionFactory));
    }

    public static <K, V, T> List<Option<K, V>> of(Collection<T> c, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return of(c.stream(), keyMapper, valueMapper, Collectors.toList());
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> C of(Collection<T> c, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        return of(c.stream(), keyMapper, valueMapper, Collectors.toCollection(collectionFactory));
    }

    public static <K, V, T> List<Option<K, V>> of(Stream<T> stream, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return of(stream, keyMapper, valueMapper, Collectors.toList());
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> C of(Stream<T> stream, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        return of(stream, keyMapper, valueMapper, Collectors.toCollection(collectionFactory));
    }

    public static <K, V, T, C> C of(Stream<T> stream, Function<T, K> keyMapper, Function<T, V> valueMapper, Collector<Option<K, V>, ?, C> collector) {
        return stream.map(t -> new Option<>(keyMapper.apply(t), valueMapper.apply(t))).collect(collector);
    }


    public static <K, V> Map<K, V> toMap(Option<K, V>[] options) {
        return toMap(options, (oldValue, newValue) -> newValue, HashMap::new);
    }

    public static <K, V> Map<K, V> toMap(Option<K, V>[] options, BinaryOperator<V> mergeFunction) {
        return toMap(options, mergeFunction, HashMap::new);
    }

    public static <K, V, M extends Map<K, V>> M toMap(Option<K, V>[] options, Supplier<M> mapFactory) {
        return toMap(options, (oldValue, newValue) -> newValue, mapFactory);
    }

    public static <K, V, M extends Map<K, V>> M toMap(Option<K, V>[] options, BinaryOperator<V> mergeFunction, Supplier<M> mapFactory) {
        return options == null
                ? mapFactory.get()
                : toMap(Arrays.stream(options), mergeFunction, mapFactory);
    }

    public static <K, V> Map<K, V> toMap(Collection<Option<K, V>> options) {
        return toMap(options, (oldValue, newValue) -> newValue, HashMap::new);
    }

    public static <K, V> Map<K, V> toMap(Collection<Option<K, V>> options, BinaryOperator<V> mergeFunction) {
        return toMap(options, mergeFunction, HashMap::new);
    }

    public static <K, V, M extends Map<K, V>> M toMap(Collection<Option<K, V>> options, Supplier<M> mapFactory) {
        return toMap(options, (oldValue, newValue) -> newValue, mapFactory);
    }

    public static <K, V, M extends Map<K, V>> M toMap(Collection<Option<K, V>> options, BinaryOperator<V> mergeFunction, Supplier<M> mapFactory) {
        return options == null
                ? mapFactory.get()
                : toMap(options.stream(), mergeFunction, mapFactory);
    }

    protected static <K, V, M extends Map<K, V>> M toMap(Stream<Option<K, V>> stream, BinaryOperator<V> mergeFunction, Supplier<M> mapFactory) {
        return stream.collect(Collectors.toMap(Option::getKey, Option::getValue, mergeFunction, mapFactory));
    }
}
