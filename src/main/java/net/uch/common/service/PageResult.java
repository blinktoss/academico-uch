package net.uch.common.service;

import java.util.List;

/**
 * Immutable page of query results plus the total matching row count, for server-side pagination.
 * {@code page} is 1-based.
 */
public final class PageResult<T> {

    private final List<T> items;
    private final long totalCount;
    private final int page;
    private final int pageSize;

    public PageResult(List<T> items, long totalCount, int page, int pageSize) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * Total number of pages ({@code >= 1}), derived from {@link #totalCount} and {@link #pageSize}.
     */
    public int getTotalPages() {
        if (pageSize <= 0 || totalCount <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
