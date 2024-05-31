package io.leego.rpa.repository;

import com.querydsl.core.types.Predicate;
import io.leego.rpa.util.Page;
import io.leego.rpa.util.Pageable;
import io.leego.rpa.util.Sortable;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@NoRepositoryBean
public interface QuerydslRepository<T> extends ListQuerydslPredicateExecutor<T> {

    default Page<T> findAll(Predicate predicate, Pageable pageable) {
        if (pageable != null) {
            if (pageable.isPaged()) {
                return ofPage(this.findAll(predicate, toPageable(pageable)));
            } else if (pageable.isSorted()) {
                return ofPage(this.findAll(predicate, toSort(pageable)));
            }
        }
        return ofPage(this.findAll(predicate));
    }

    default List<T> findAll(Predicate predicate, Sortable sortable) {
        if (sortable != null && sortable.isSorted()) {
            return toList(this.findAll(predicate, toSort(sortable)));
        }
        return toList(this.findAll(predicate));
    }

    private Page<T> ofPage(Iterable<T> iterable) {
        return Page.of(toList(iterable));
    }

    private Page<T> ofPage(org.springframework.data.domain.Page<T> page) {
        return Page.of(page.getContent(),
                page.getPageable().getPageNumber() + 1,
                page.getPageable().getPageSize(),
                page.getTotalElements(),
                (long) page.getTotalPages());
    }

    private org.springframework.data.domain.Pageable toPageable(Pageable pageable) {
        return org.springframework.data.domain.PageRequest.of(
                pageable.getPage() - 1,
                pageable.getSize(),
                toSort(pageable));
    }

    private org.springframework.data.domain.Sort toSort(Sortable sortable) {
        if (!sortable.isSorted()) {
            return org.springframework.data.domain.Sort.unsorted();
        }
        return org.springframework.data.domain.Sort.by(
                sortable.getSort()
                        .getOrders()
                        .stream()
                        .map(o -> new org.springframework.data.domain.Sort.Order(
                                o.getDirection() == null
                                        ? org.springframework.data.domain.Sort.DEFAULT_DIRECTION
                                        : org.springframework.data.domain.Sort.Direction.fromString(o.getDirection().name()),
                                o.getProperty()))
                        .collect(Collectors.toList()));
    }

    private List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List<T> list) {
            return list;
        } else if (iterable instanceof Collection<T> collection) {
            return new ArrayList<>(collection);
        } else {
            List<T> list = new ArrayList<>();
            iterable.forEach(list::add);
            return list;
        }
    }
}
