package com.eagle.boot.config.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EagleDataPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long count;
    private final Long total; // The total may not be known sometimes, so it's optional
    private final int offset;
    private final int limit;
    private final List<T> data;

    public EagleDataPage(Long total, int offset, int limit) {
        this(null, total, offset, limit);
    }

    public EagleDataPage(List<T> data, int offset, int limit) {
        this(data, null, offset, limit);
    }

    public EagleDataPage(List<T> data, Long total, int offset, int limit) {
        if ( offset < 0 ) { throw new IllegalArgumentException("Provided offset ["+offset+"] is invalid"); }
        if ( limit < 0 ) { throw new IllegalArgumentException("Provided limit ["+limit+"] is invalid"); }
        if ( data != null ) {
            this.count = data.size();
            this.data = Collections.unmodifiableList(data);
        } else {
            this.count = 0;
            this.data = Collections.unmodifiableList(new ArrayList<>());
        }
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public boolean isEmpty() {
        return this.count <= 0;
    }

    public long getCount() {
        return count;
    }

    public Long getTotal() {
        return total;
    }

    /**
     * Returns a list that won't allow mutations
     */
    public List<T> getData() {
        return data;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EagleDataPage<?> that = (EagleDataPage<?>) o;

        if (count != that.count) return false;
        if (offset != that.offset) return false;
        if (limit != that.limit) return false;
        if (total != null ? !total.equals(that.total) : that.total != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (count ^ (count >>> 32));
        result = 31 * result + (total != null ? total.hashCode() : 0);
        result = 31 * result + offset;
        result = 31 * result + limit;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EagleDataPage{" +
                "total=" + total +
                ", offset=" + offset +
                ", limit=" + limit +
                ", data=" + data +
                ", count=" + count +
                '}';
    }
}
