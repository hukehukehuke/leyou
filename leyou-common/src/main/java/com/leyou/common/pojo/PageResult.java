package com.leyou.common.pojo;

import java.util.List;

public class PageResult<T> {

    private Long total;
    private Integer totalPage;
    private List<T> item;

    public PageResult() {
    }

    public PageResult(Long total, Integer totalPage) {
        this.total = total;
        this.totalPage = totalPage;
    }

    public PageResult(Long total, List<T> item) {
        this.total = total;
        this.item = item;
    }

    public PageResult(Long total, Integer totalPage, List<T> item) {
        this.total = total;
        this.totalPage = totalPage;
        this.item = item;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItem() {
        return item;
    }

    public void setItem(List<T> item) {
        this.item = item;
    }
}
