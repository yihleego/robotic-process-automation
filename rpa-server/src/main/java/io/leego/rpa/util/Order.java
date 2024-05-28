package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Leego Yih
 */
public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = -6757591751151983159L;
    private static final boolean DEFAULT_IGNORE_CASE = false;
    private static final Direction DEFAULT_DIRECTION = Direction.ASC;
    private static final String IGNORECASE = "ignorecase";

    private Direction direction;
    private String property;
    private boolean ignoreCase;

    public Order() {
    }

    public Order(String property) {
        this(DEFAULT_DIRECTION, property, DEFAULT_IGNORE_CASE);
    }

    public Order(Direction direction, String property) {
        this(direction, property, DEFAULT_IGNORE_CASE);
    }

    public Order(Direction direction, String property, boolean ignoreCase) {
        if (property == null) {
            throw new IllegalArgumentException("Property must not null or empty!");
        }
        this.direction = direction == null ? DEFAULT_DIRECTION : direction;
        this.property = property;
        this.ignoreCase = ignoreCase;
    }

    public static Order by(String property) {
        return new Order(DEFAULT_DIRECTION, property);
    }

    public static Order asc(String property) {
        return new Order(Direction.ASC, property);
    }

    public static Order desc(String property) {
        return new Order(Direction.DESC, property);
    }

    public static Order parse(String value) {
        int i = value.indexOf(':');
        if (i == -1) {
            return Order.by(value.strip());
        }
        int j = value.indexOf(':', i + 1);
        boolean ignoreCase = j != -1;
        if (ignoreCase && !IGNORECASE.equalsIgnoreCase(value.substring(j + 1).strip())) {
            throw new IllegalArgumentException("Invalid order");
        }
        String property = value.substring(0, i).strip();
        if (property.isEmpty()) {
            throw new IllegalArgumentException("Invalid property '%s'".formatted(property));
        }
        String direction = ignoreCase ? value.substring(i + 1, j).strip() : value.substring(i + 1).strip();
        return new Order(Direction.fromString(direction), property, ignoreCase);
    }

    public static String format(Order order) {
        return order.isIgnoreCase()
                ? (order.getProperty() + ":" + order.getDirection() + ":" + IGNORECASE)
                : (order.getProperty() + ":" + order.getDirection());
    }

    public Direction getDirection() {
        return direction;
    }

    public String getProperty() {
        return property;
    }

    public boolean isAscending() {
        return this.direction.isAscending();
    }

    public boolean isDescending() {
        return this.direction.isDescending();
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public Order with(Direction direction) {
        return new Order(direction, this.property, this.ignoreCase);
    }

    public Order withProperty(String property) {
        return new Order(this.direction, property, this.ignoreCase);
    }

    public Sort withProperties(String... properties) {
        return Sort.by(this.direction, properties);
    }

    public Order ignoreCase() {
        return new Order(direction, property, true);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + direction.hashCode();
        result = 31 * result + property.hashCode();
        result = 31 * result + (ignoreCase ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Order that)) {
            return false;
        }
        return this.direction.equals(that.direction)
                && this.property.equals(that.property)
                && this.ignoreCase == that.ignoreCase;
    }

    @Override
    public String toString() {
        String result = String.format("%s: %s", property, direction);
        if (ignoreCase) {
            result += ", ignoring case";
        }
        return result;
    }
}
