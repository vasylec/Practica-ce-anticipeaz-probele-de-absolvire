package com.example.data.entity;

public enum PageSize {
    PAGE_SIZE_10(10, "10"),
    PAGE_SIZE_20(20, "20"),
    PAGE_SIZE_30(30, "30"),
    PAGE_SIZE_50(50, "50"),
    PAGE_SIZE_100(100, "100"),
    PAGE_SIZE_ALL(-1, "All");

    private final int pageSize;
    private final String label;

    PageSize(int pageSize, String label){
        this.pageSize = pageSize;
        this.label = label;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getLabel() {
        return label;
    }
}
