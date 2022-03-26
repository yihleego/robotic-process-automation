package io.leego.rpa.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Leego Yih
 */
public class SortRequest implements Sortable, Serializable {
    @Serial
    private static final long serialVersionUID = -8453615821595321534L;
    /** The sorting parameters. */
    protected Sort sort;

    public SortRequest() {
    }

    public SortRequest(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public boolean isSorted() {
        return sort != null && sort.isSorted();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof SortRequest that))
            return false;
        return Objects.equals(sort, that.sort);
    }

    @Override
    public int hashCode() {
        return sort != null ? sort.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("Sort request [sort: %s]", this.sort);
    }
}
