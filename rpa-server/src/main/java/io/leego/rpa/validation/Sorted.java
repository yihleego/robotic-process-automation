package io.leego.rpa.validation;

import io.leego.rpa.util.Sort;
import io.leego.rpa.util.Sortable;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Set;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Leego Yih
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE})
@Retention(RUNTIME)
@Repeatable(Sorted.List.class)
@Documented
@Constraint(validatedBy = {Sorted.SortableValidator.class, Sorted.SortValidator.class})
public @interface Sorted {

    String[] properties() default {};

    String message() default "{jakarta.validation.constraints.Sorted.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Sorted[] value();
    }

    class SortableValidator implements ConstraintValidator<Sorted, Sortable> {
        private Set<String> properties;

        @Override
        public void initialize(Sorted sorted) {
            this.properties = Set.of(sorted.properties());
        }

        @Override
        public boolean isValid(Sortable sortable, ConstraintValidatorContext context) {
            if (sortable == null || !sortable.isSorted()) {
                return true;
            }
            return sortable.getSort().getOrders().stream().allMatch(o -> this.properties.contains(o.getProperty()));
        }
    }

    class SortValidator implements ConstraintValidator<Sorted, Sort> {
        private Set<String> properties;

        @Override
        public void initialize(Sorted sorted) {
            this.properties = Set.of(sorted.properties());
        }

        @Override
        public boolean isValid(Sort sort, ConstraintValidatorContext context) {
            if (sort == null || !sort.isSorted()) {
                return true;
            }
            return sort.getOrders().stream().allMatch(order -> this.properties.contains(order.getProperty()));
        }
    }
}

