package com.dt.jdbc.bean;

/**
 * @author 白超
 * @version 1.0
 * @since 2018/7/11
 */
public abstract class AbstractPageResult {

    protected Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
