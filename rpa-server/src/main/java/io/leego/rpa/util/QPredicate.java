package io.leego.rpa.util;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
public class QPredicate implements Predicate, Cloneable {
    /** Current predicate */
    private Predicate predicate;

    /**
     * Create an empty PredicateBuilder
     */
    public QPredicate() {
    }

    /**
     * Create a PredicateBuilder with the given initial value
     *
     * @param initial initial value
     */
    public QPredicate(Predicate initial) {
        predicate = (Predicate) ExpressionUtils.extract(initial);
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        if (predicate != null) {
            return predicate.accept(v, context);
        } else {
            return null;
        }
    }

    @Override
    public Class<? extends Boolean> getType() {
        return Boolean.class;
    }

    /**
     * Get the negation of the expression
     *
     * @return negation
     */
    public QPredicate not() {
        if (predicate != null) {
            predicate = predicate.not();
        }
        return this;
    }

    /**
     * Create the intersection of this and the given predicate
     *
     * @param right right hand side of {@code and} operation
     * @return the current object
     */
    public QPredicate and(Predicate right) {
        if (right != null) {
            if (predicate == null) {
                predicate = right;
            } else {
                predicate = ExpressionUtils.and(predicate, right);
            }
        }
        return this;
    }

    /**
     * Create the intersection of this and the union of the given args
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAllOf(Predicate... args) {
        if (args.length > 0) {
            and(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Create the intersection of this and the union of the given args
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAllOf(List<Predicate> args) {
        if (args.size() > 0) {
            and(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Create the intersection of this and the union of the given args
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAnyOf(Predicate... args) {
        if (args.length > 0) {
            and(ExpressionUtils.anyOf(args));
        }
        return this;
    }

    /**
     * Create the intersection of this and the union of the given args
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param args union of predicates
     * @return the current object
     */
    public QPredicate andAnyOf(List<Predicate> args) {
        if (args.size() > 0) {
            and(ExpressionUtils.anyOf(args));
        }
        return this;
    }

    /**
     * Create the insertion of this and the negation of the given predicate
     *
     * @param right predicate to be negated
     * @return the current object
     */
    public QPredicate andNot(Predicate right) {
        return and(right.not());
    }

    /**
     * Create the union of this and the given predicate
     *
     * @param right right hand side of {@code or} operation
     * @return the current object
     */
    public QPredicate or(Predicate right) {
        if (right != null) {
            if (predicate == null) {
                predicate = right;
            } else {
                predicate = ExpressionUtils.or(predicate, right);
            }
        }
        return this;
    }

    /**
     * Create the union of this and the intersection of the given args
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param args intersection of predicates
     * @return the current object
     */
    public QPredicate orAllOf(Predicate... args) {
        if (args.length > 0) {
            or(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Create the union of this and the intersection of the given args
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param args intersection of predicates
     * @return the current object
     */
    public QPredicate orAllOf(List<Predicate> args) {
        if (args.size() > 0) {
            or(ExpressionUtils.allOf(args));
        }
        return this;
    }

    /**
     * Create the union of this and the negation of the given predicate
     *
     * @param right predicate to be negated
     * @return the current object
     */
    public QPredicate orNot(Predicate right) {
        return or(right.not());
    }

    /**
     * Create the intersection of this and the given operation and value
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate and(Function<V, Predicate> operation, V value) {
        return value != null && operation != null
                ? and(operation.apply(value))
                : this;

    }

    /**
     * Create the intersection of this and the given operation and values
     *
     * @param operation the operation
     * @param value1    the first value
     * @param value2    the second value
     * @return the current object
     */
    public <V1, V2> QPredicate and(BiFunction<V1, V2, Predicate> operation, V1 value1, V2 value2) {
        return (value1 != null || value2 != null) && operation != null
                ? and(operation.apply(value1, value2))
                : this;
    }

    /**
     * Create the intersection of this and the union of the given operation and values
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAllOf(Function<V, Predicate> operation, V... values) {
        return values != null && values.length > 0 && operation != null
                ? andAllOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the intersection of this and the union of the given operation and values
     * {@code (this && (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAllOf(Function<V, Predicate> operation, Collection<V> values) {
        return values != null && values.size() > 0 && operation != null
                ? andAllOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the intersection of this and the union of the given operation and values
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAnyOf(Function<V, Predicate> operation, V... values) {
        return values != null && values.length > 0 && operation != null
                ? andAnyOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the intersection of this and the union of the given operation and values
     * {@code (this && (arg1 || arg2 ... || argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate andAnyOf(Function<V, Predicate> operation, Collection<V> values) {
        return values != null && values.size() > 0 && operation != null
                ? andAnyOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the insertion of this and the negation of the given operation and value
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate andNot(Function<V, Predicate> operation, V value) {
        return value != null && operation != null
                ? andNot(operation.apply(value))
                : this;
    }

    /**
     * Create the union of this and the given operation and value
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate or(Function<V, Predicate> operation, V value) {
        return value != null && operation != null
                ? or(operation.apply(value))
                : this;
    }

    /**
     * Create the union of this and the intersection of the given operation and values
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate orAllOf(Function<V, Predicate> operation, V... values) {
        return values != null && values.length > 0 && operation != null
                ? orAllOf(Arrays.stream(values).filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the union of this and the intersection of the given operation and values
     * {@code (this || (arg1 && arg2 ... && argN))}
     *
     * @param operation the operation
     * @param values    the values
     * @return the current object
     */
    public <V> QPredicate orAllOf(Function<V, Predicate> operation, Collection<V> values) {
        return values != null && values.size() > 0 && operation != null
                ? orAllOf(values.stream().filter(Objects::nonNull).map(operation).collect(Collectors.toList()))
                : this;
    }

    /**
     * Create the union of this and the negation of the given operation and value
     *
     * @param operation the operation
     * @param value     the value
     * @return the current object
     */
    public <V> QPredicate orNot(Function<V, Predicate> operation, V value) {
        return value != null && operation != null
                ? orNot(operation.apply(value))
                : this;
    }

    /**
     * Returns <code>true</code> if the value is set, and false, if not
     *
     * @return true if initialized and false if not
     */
    public boolean isPresent() {
        return predicate != null;
    }

    /**
     * Returns <code>true</code> if the value is null, and false, if not
     *
     * @return true if absent and false if not
     */
    public boolean isEmpty() {
        return predicate == null;
    }

    public Optional<Predicate> optional() {
        return Optional.ofNullable(predicate);
    }

    @Override
    public QPredicate clone() throws CloneNotSupportedException {
        return (QPredicate) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof QPredicate) {
            return Objects.equals(((QPredicate) o).predicate, predicate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return predicate != null ? predicate.hashCode() : 0;
    }

    @Override
    public String toString() {
        return predicate != null ? predicate.toString() : super.toString();
    }

    public static QPredicate create() {
        return new QPredicate();
    }

    public static QPredicate from(Predicate initial) {
        return new QPredicate(initial);
    }
}
