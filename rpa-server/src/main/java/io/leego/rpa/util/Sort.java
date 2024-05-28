package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
public class Sort implements Serializable {
    @Serial
    private static final long serialVersionUID = -7445270943868879346L;
    private static final Sort UNSORTED = new Sort();
    private static final Direction DEFAULT_DIRECTION = Direction.ASC;

    private List<Order> orders;

    protected Sort() {
        this.orders = Collections.emptyList();
    }

    protected Sort(List<Order> orders) {
        this.orders = orders;
    }

    protected Sort(Direction direction, List<String> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }
        this.orders = properties.stream()
                .map(property -> new Order(direction, property))
                .collect(Collectors.toList());
    }

    public static Sort by(String property) {
        return new Sort(Collections.singletonList(new Order(property)));
    }

    public static Sort by(String... properties) {
        return by(DEFAULT_DIRECTION, properties);
    }

    public static Sort by(Direction direction, String... properties) {
        if (properties == null || properties.length == 0) {
            return Sort.unsorted();
        }
        List<Order> orders = new ArrayList<>(properties.length);
        for (String property : properties) {
            orders.add(new Order(direction, property));
        }
        return new Sort(orders);
    }

    public static Sort by(Order order) {
        return new Sort(Collections.singletonList(order));
    }

    public static Sort by(Order... orders) {
        return orders == null || orders.length == 0 ? Sort.unsorted() : new Sort(Arrays.asList(orders));
    }

    public static Sort by(List<Order> orders) {
        return orders == null || orders.isEmpty() ? Sort.unsorted() : new Sort(orders);
    }

    public static Sort unsorted() {
        return UNSORTED;
    }

    public static Sort parse(String value) {
        if (value == null || value.isBlank()) {
            return Sort.unsorted();
        }
        if (value.indexOf(',') > 0) {
            return Sort.by(Arrays.stream(value.split(","))
                    .filter(o -> o != null && !o.isBlank())
                    .map(Order::parse)
                    .collect(Collectors.toList()));
        } else {
            return Sort.by(Order.parse(value));
        }
    }

    public static String format(Sort sort) {
        if (sort == null) {
            return null;
        }
        return sort.getOrders()
                .stream()
                .map(Order::format)
                .collect(Collectors.joining(","));
    }

    public Sort descending() {
        return withDirection(Direction.DESC);
    }

    public Sort ascending() {
        return withDirection(Direction.ASC);
    }

    public boolean isSorted() {
        return !isEmpty();
    }

    public boolean isUnsorted() {
        return !isSorted();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public Sort and(Sort sort) {
        if (sort == null) {
            return Sort.by(this.orders);
        }
        ArrayList<Order> these = new ArrayList<>(this.orders);
        these.addAll(sort.orders);
        return Sort.by(these);
    }

    private Sort withDirection(Direction direction) {
        return Sort.by(this.orders.stream().map(it -> new Order(direction, it.getProperty())).collect(Collectors.toList()));
    }

    public Order getOrderFor(String property) {
        for (Order order : this.orders) {
            if (order.getProperty().equals(property)) {
                return order;
            }
        }
        return null;
    }

    public Iterator<Order> iterator() {
        return this.orders.iterator();
    }

    public List<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Sort that)) {
            return false;
        }
        return orders.equals(that.orders);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + orders.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return isEmpty() ? "UNSORTED" : orders.toString();
    }
}
