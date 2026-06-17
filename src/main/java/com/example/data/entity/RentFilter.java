package com.example.data.entity;

public enum RentFilter {
    ACTIVE("Show Active Rents"),
    FUTURE("Show Future Rents"),
    ALL("Show All Rents");

    private final String label;

    RentFilter(String label){
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
