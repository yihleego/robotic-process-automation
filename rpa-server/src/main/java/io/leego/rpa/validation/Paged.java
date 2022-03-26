package io.leego.rpa.validation;

import io.leego.rpa.util.Pageable;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

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
@Repeatable(Paged.List.class)
@Documented
@Constraint(validatedBy = {Paged.PageableValidator.class})
public @interface Paged {

    int maxPage() default -1;

    int maxSize() default -1;

    String message() default "{jakarta.validation.constraints.Paged.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        Paged[] value();
    }

    class PageableValidator implements ConstraintValidator<Paged, Pageable> {
        private int maxPage;
        private int maxSize;

        @Override
        public void initialize(Paged paged) {
            this.maxPage = paged.maxPage();
            this.maxSize = paged.maxSize();
        }

        @Override
        public boolean isValid(Pageable pageable, ConstraintValidatorContext context) {
            return pageable != null
                    && pageable.isPaged()
                    && (this.maxPage <= 0 || pageable.getPage() <= this.maxPage)
                    && (this.maxSize <= 0 || pageable.getSize() <= this.maxSize);
        }
    }
}

