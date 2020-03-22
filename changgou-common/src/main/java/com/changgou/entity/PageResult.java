package com.changgou.entity;

import java.util.List;

/**
 * 分页查询结果的类
 */
public class PageResult<T> {
    private Long total;//总记录数
    private List<T> rows;//记录

    //满参
    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }
    //空参
    public PageResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
