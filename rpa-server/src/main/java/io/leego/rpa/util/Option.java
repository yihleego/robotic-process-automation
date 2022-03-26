package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
public class Option<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = -121222596239767866L;
    private K key;
    private V value;

    public Option() {
    }

    public Option(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Option<K, V> of(K key, V value) {
        return new Option<>(key, value);
    }

    public static <K, V> List<Option<K, V>> of(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }
        List<Option<K, V>> optionList = new ArrayList<>(map.size());
        map.forEach((k, v) -> optionList.add(of(k, v)));
        return optionList;
    }

    public static <K, V, X, Y> List<Option<K, V>> of(Map<X, Y> map, Function<X, K> keyMapper, Function<Y, V> valueMapper) {
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }
        List<Option<K, V>> optionList = new ArrayList<>(map.size());
        map.forEach((k, v) -> optionList.add(of(keyMapper.apply(k), valueMapper.apply(v))));
        return optionList;
    }


    public static <K, V, T> List<Option<K, V>> of(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return of(array, keyMapper, valueMapper, ArrayList::new);
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> C of(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        if (array == null || array.length == 0) {
            return collectionFactory.get();
        }
        return Arrays.stream(array)
                .map(t -> new Option<>(keyMapper.apply(t), valueMapper.apply(t)))
                .collect(Collectors.toCollection(collectionFactory));
    }

    public static <K, V, T> List<Option<K, V>> of(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return of(collection, keyMapper, valueMapper, ArrayList::new);
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> C of(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        if (collection == null || collection.isEmpty()) {
            return collectionFactory.get();
        }
        return collection.stream()
                .map(t -> new Option<>(keyMapper.apply(t), valueMapper.apply(t)))
                .collect(Collectors.toCollection(collectionFactory));
    }


    public static <K, V, T> Map<K, List<Option<K, V>>> groupingBy(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return groupingBy(array, keyMapper, valueMapper, Option::getKey, ArrayList::new);
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> Map<K, C> groupingBy(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        return groupingBy(array, keyMapper, valueMapper, Option::getKey, collectionFactory);
    }

    public static <K, V, T, U> Map<U, List<Option<K, V>>> groupingBy(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper) {
        return groupingBy(array, keyMapper, valueMapper, groupMapper, ArrayList::new);
    }

    public static <K, V, T, U, C extends Collection<Option<K, V>>> Map<U, C> groupingBy(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper, Supplier<C> collectionFactory) {
        return groupingBy(array, keyMapper, valueMapper, groupMapper, collectionFactory, LinkedHashMap::new);
    }

    public static <K, V, T, U, C extends Collection<Option<K, V>>, M extends Map<U, C>> M groupingBy(T[] array, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper, Supplier<C> collectionFactory, Supplier<M> mapFactory) {
        if (array == null || array.length == 0) {
            return mapFactory.get();
        }
        return Arrays.stream(array)
                .map(t -> new Option<>(keyMapper.apply(t), valueMapper.apply(t)))
                .collect(Collectors.groupingBy(
                        groupMapper,
                        mapFactory,
                        Collectors.toCollection(collectionFactory)));
    }

    public static <K, V, T> Map<K, List<Option<K, V>>> groupingBy(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return groupingBy(collection, keyMapper, valueMapper, Option::getKey, ArrayList::new);
    }

    public static <K, V, T, C extends Collection<Option<K, V>>> Map<K, C> groupingBy(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, Supplier<C> collectionFactory) {
        return groupingBy(collection, keyMapper, valueMapper, Option::getKey, collectionFactory);
    }

    public static <K, V, T, U> Map<U, List<Option<K, V>>> groupingBy(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper) {
        return groupingBy(collection, keyMapper, valueMapper, groupMapper, ArrayList::new);
    }

    public static <K, V, T, U, C extends Collection<Option<K, V>>> Map<U, C> groupingBy(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper, Supplier<C> collectionFactory) {
        return groupingBy(collection, keyMapper, valueMapper, groupMapper, collectionFactory, LinkedHashMap::new);
    }

    public static <K, V, T, U, C extends Collection<Option<K, V>>, M extends Map<U, C>> M groupingBy(Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper, Function<Option<K, V>, U> groupMapper, Supplier<C> collectionFactory, Supplier<M> mapFactory) {
        if (collection == null || collection.isEmpty()) {
            return mapFactory.get();
        }
        return collection.stream()
                .map(t -> new Option<>(keyMapper.apply(t), valueMapper.apply(t)))
                .collect(Collectors.groupingBy(
                        groupMapper,
                        mapFactory,
                        Collectors.toCollection(collectionFactory)));
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
        if (options == null || options.length == 0) {
            return mapFactory.get();
        }
        return Arrays.stream(options).collect(
                Collectors.toMap(
                        Option::getKey,
                        Option::getValue,
                        mergeFunction,
                        mapFactory));
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
        if (options == null || options.isEmpty()) {
            return mapFactory.get();
        }
        return options.stream().collect(
                Collectors.toMap(
                        Option::getKey,
                        Option::getValue,
                        mergeFunction,
                        mapFactory));
    }


    public static <K, V, X, Y> Map<K, V> toMap(Option<X, Y>[] options, Function<X, K> keyMapper, Function<Y, V> valueMapper) {
        return toMap(options, keyMapper, valueMapper, (oldValue, newValue) -> newValue, HashMap::new);
    }

    public static <K, V, X, Y> Map<K, V> toMap(Option<X, Y>[] options, Function<X, K> keyMapper, Function<Y, V> valueMapper, BinaryOperator<V> mergeFunction) {
        return toMap(options, keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    public static <K, V, X, Y, M extends Map<K, V>> M toMap(Option<X, Y>[] options, Function<X, K> keyMapper, Function<Y, V> valueMapper, Supplier<M> mapFactory) {
        return toMap(options, keyMapper, valueMapper, (oldValue, newValue) -> newValue, mapFactory);
    }

    public static <K, V, X, Y, M extends Map<K, V>> M toMap(Option<X, Y>[] options, Function<X, K> keyMapper, Function<Y, V> valueMapper, BinaryOperator<V> mergeFunction, Supplier<M> mapFactory) {
        if (options == null || options.length == 0) {
            return mapFactory.get();
        }
        return Arrays.stream(options).collect(
                Collectors.toMap(
                        o -> keyMapper.apply(o.getKey()),
                        o -> valueMapper.apply(o.getValue()),
                        mergeFunction,
                        mapFactory));
    }

    public static <K, V, X, Y> Map<K, V> toMap(Collection<Option<X, Y>> options, Function<X, K> keyMapper, Function<Y, V> valueMapper) {
        return toMap(options, keyMapper, valueMapper, (oldValue, newValue) -> newValue, HashMap::new);
    }

    public static <K, V, X, Y> Map<K, V> toMap(Collection<Option<X, Y>> options, Function<X, K> keyMapper, Function<Y, V> valueMapper, BinaryOperator<V> mergeFunction) {
        return toMap(options, keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    public static <K, V, X, Y, M extends Map<K, V>> M toMap(Collection<Option<X, Y>> options, Function<X, K> keyMapper, Function<Y, V> valueMapper, Supplier<M> mapFactory) {
        return toMap(options, keyMapper, valueMapper, (oldValue, newValue) -> newValue, mapFactory);
    }

    public static <K, V, X, Y, M extends Map<K, V>> M toMap(Collection<Option<X, Y>> options, Function<X, K> keyMapper, Function<Y, V> valueMapper, BinaryOperator<V> mergeFunction, Supplier<M> mapFactory) {
        if (options == null || options.isEmpty()) {
            return mapFactory.get();
        }
        return options.stream().collect(
                Collectors.toMap(
                        o -> keyMapper.apply(o.getKey()),
                        o -> valueMapper.apply(o.getValue()),
                        mergeFunction,
                        mapFactory));
    }


    public <X, Y> Option<X, Y> map(Function<? super K, ? extends X> keyConverter, Function<? super V, ? extends Y> valueConverter) {
        return new Option<>(keyConverter.apply(key), valueConverter.apply(value));
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}
