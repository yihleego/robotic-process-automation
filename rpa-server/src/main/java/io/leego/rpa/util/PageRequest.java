package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Leego Yih
 */
public class PageRequest extends SortRequest implements Pageable, Serializable {
    @Serial
    private static final long serialVersionUID = -542101357510265940L;
    /** Zero-based page index. */
    protected Integer page;
    /** The size of the page to be returned. */
    protected Integer size;

    public PageRequest() {
    }

    public PageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public PageRequest(Integer page, Integer size, Sort sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public Long getOffset() {
        if (page == null || size == null) {
            return null;
        }
        return (long) page * size;
    }

    @Override
    public boolean isPaged() {
        return page != null && size != null && page >= 0 && size > 0;
    }

    @Override
    public PageRequest next() {
        return new PageRequest(getPage() + 1, getSize(), getSort());
    }

    @Override
    public PageRequest previous() {
        return getPage() == 0 ? this : new PageRequest(getPage() - 1, getSize(), getSort());
    }

    @Override
    public PageRequest first() {
        return new PageRequest(0, getSize(), getSort());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof PageRequest that))
            return false;
        if (!super.equals(o))
            return false;
        return Objects.equals(page, that.page) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format("Page request [page: %d, size %d, sort: %s]", this.page, this.size, this.sort);
    }
}
