package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Leego Yih
 */
public class Page<T> implements Serializable, Iterable<T> {
    @Serial
    private static final long serialVersionUID = 3214571808482585491L;
    /** The page content as {@link List}. */
    protected List<T> list;
    /** The page to be returned, zero-based page index, must be greater than zero. */
    protected Integer page;
    /** The size of the page to be returned, must be greater than zero. */
    protected Integer size;
    /** The number of elements. */
    protected Long total;
    /** The number of pages. */
    protected Long pages;
    /** The extra data. */
    protected Object extra;

    public Page() {
    }

    public Page(List<T> list) {
        this.list = list;
    }

    public Page(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public Page(List<T> list, Integer page, Integer size) {
        this.list = list;
        this.page = page;
        this.size = size;
    }

    public Page(List<T> list, Integer page, Integer size, Long total, Long pages) {
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = pages;
    }

    public Page(List<T> list, Integer page, Integer size, Long total, Long pages, Object extra) {
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.extra = extra;
    }

    public static <T> Page<T> of(Collection<T> collection) {
        if (collection == null) {
            return new Page<>(Collections.emptyList());
        }
        if (collection instanceof List) {
            return new Page<>((List<T>) collection);
        }
        return new Page<>(new ArrayList<>(collection));
    }

    public static <T> Page<T> of(List<T> list) {
        return new Page<>(list);
    }

    public static <T> Page<T> of(List<T> list, Long total) {
        return new Page<>(list, total);
    }

    public static <T> Page<T> of(List<T> list, Integer page, Integer size) {
        return new Page<>(list, page, size);
    }

    public static <T> Page<T> of(List<T> list, Integer page, Integer size, Long total) {
        if (page == null || size == null) {
            return new Page<>(list, total);
        }
        if (total == null) {
            return new Page<>(list, page, size);
        }
        long pages;
        if (page >= 0 && size > 0) {
            pages = total % size > 0 ? total / size + 1 : total / size;
        } else {
            pages = 0L;
        }
        return new Page<>(list, page, size, total, pages);
    }

    public static <T> Page<T> of(List<T> list, Integer page, Integer size, Long total, Long pages) {
        return new Page<>(list, page, size, total, pages);
    }

    public static <T> Page<T> of(List<T> list, Integer page, Integer size, Long total, Long pages, Object extra) {
        return new Page<>(list, page, size, total, pages, extra);
    }

    public static <T, U> Page<U> empty(Page<T> page) {
        if (page == null) {
            return new Page<>(Collections.emptyList());
        }
        return new Page<>(Collections.emptyList(), page.page, page.size, page.total, page.pages, page.extra);
    }

    public static <T> Page<T> empty(Pageable pageable) {
        return new Page<>(Collections.emptyList(), pageable.getPage(), pageable.getSize());
    }

    public static <T> Page<T> empty() {
        return new Page<>(Collections.emptyList());
    }

    public <U> Page<U> toEmpty() {
        return new Page<>(Collections.emptyList(), page, size, total, pages, extra);
    }

    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new Page<>(
                list == null || list.isEmpty() ? Collections.emptyList() : list.stream().map(converter).collect(Collectors.toList()),
                page, size, total, pages, extra);
    }

    public boolean addAll(Collection<? extends T> c) {
        if (list == null) {
            list = new ArrayList<>();
        }
        return list.addAll(c);
    }

    public void clear() {
        if (list != null) {
            list.clear();
        }
    }

    public Page<T> peek(Consumer<? super T> action) {
        if (list != null) {
            list.forEach(action);
        }
        return this;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        if (list != null) {
            list.forEach(action);
        }
    }

    @Override
    public Iterator<T> iterator() {
        if (list == null) {
            return Collections.emptyIterator();
        }
        return list.iterator();
    }

    public Stream<T> stream() {
        if (list == null) {
            return Stream.empty();
        }
        return list.stream();
    }

    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public boolean isPaged() {
        return list != null && page != null && size != null && total != null;
    }

    public boolean hasPrevious() {
        return page != null && size != null && page > 0 && size > 0;
    }

    public boolean hasNext() {
        return page != null && size != null && (long) page * size < total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public <E> E getExtra(Class<E> clazz) {
        return clazz.cast(extra);
    }
}
