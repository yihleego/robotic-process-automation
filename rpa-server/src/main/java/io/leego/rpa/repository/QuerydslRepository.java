package io.leego.rpa.repository;

import com.querydsl.core.types.Predicate;
import io.leego.rpa.util.Order;
import io.leego.rpa.util.Page;
import io.leego.rpa.util.Pageable;
import io.leego.rpa.util.Sortable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leego Yih
 */
@NoRepositoryBean
public interface QuerydslRepository<T> extends QuerydslPredicateExecutor<T> {

    default Page<T> findAll(Predicate predicate, Pageable pageable) {
        if (pageable.isPaged()) {
            return toPage(this.findAll(predicate, toPageable(pageable)));
        }
        if (pageable.isSorted()) {
            return toPage(this.findAll(predicate, toSort(pageable)));
        }
        return toPage(this.findAll(predicate));
    }

    default List<T> findAll(Predicate predicate, Sortable sortable) {
        if (sortable.isSorted()) {
            return toList(this.findAll(predicate, toSort(sortable)));
        }
        return toList(this.findAll(predicate));
    }

    private org.springframework.data.domain.Pageable toPageable(Pageable pageable) {
        return pageable.isSorted()
                ? org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize(), toSort(pageable))
                : org.springframework.data.domain.PageRequest.of(pageable.getPage(), pageable.getSize());
    }

    private org.springframework.data.domain.Sort toSort(Sortable sortable) {
        return org.springframework.data.domain.Sort.by(
                sortable.getSort()
                        .getOrders()
                        .stream()
                        .map(this::toOrder)
                        .collect(Collectors.toList()));
    }

    private org.springframework.data.domain.Sort.Order toOrder(Order order) {
        org.springframework.data.domain.Sort.Order o = order.getDirection().isAscending()
                ? org.springframework.data.domain.Sort.Order.asc(order.getProperty())
                : org.springframework.data.domain.Sort.Order.desc(order.getProperty());
        return order.isIgnoreCase() ? o.ignoreCase() : o;
    }

    private Page<T> toPage(Iterable<T> iterable) {
        return Page.of(toList(iterable));
    }

    private Page<T> toPage(org.springframework.data.domain.Page<T> page) {
        return Page.of(
                page.getContent(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize(),
                page.getTotalElements(),
                (long) page.getTotalPages());
    }

    private List<T> toList(Iterable<T> iterable) {
        if (iterable instanceof List<T> list) {
            return list;
        }
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
