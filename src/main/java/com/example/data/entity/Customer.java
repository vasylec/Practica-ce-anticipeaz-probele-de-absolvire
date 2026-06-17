package com.example.data.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "customer",
        indexes = {
                @Index(name = "idx_customer_first_name", columnList = "first_name"),
                @Index(name = "idx_customer_second_name", columnList = "second_name"),
                @Index(name = "idx_customer_phone", columnList = "phone")
        }
)
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    private String firstName;
    private String secondName;
    private int totalRentals;
    private int lateReturns;
    private String phone;

    public Customer() {
    }

    public Customer(String customerName, String customerSecondName, int totalRentals, int lateReturns, String phone) {
        this.firstName = customerName;
        this.secondName = customerSecondName;
        this.totalRentals = totalRentals;
        this.lateReturns = lateReturns;
        this.phone = phone;
    }

    public String getInfo(){
        return firstName + " " + secondName;
    }

    public String getCustomerName() {
        return firstName;
    }

    public void setCustomerName(String customerName) {
        this.firstName = customerName;
    }

    public String getCustomerSecondName() {
        return secondName;
    }

    public void setCustomerSecondName(String customerSecondName) {
        this.secondName = customerSecondName;
    }

    public int getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
    }

    public int getLateReturns() {
        return lateReturns;
    }

    public void setLateReturns(int lateReturns) {
        this.lateReturns = lateReturns;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(Long id) {
        this.customerId = id;
    }

    public Long getId() {
        return customerId;
    }

    public void incrementTotalRentals() {
        totalRentals++;
    }

    public void decrementTotalRentals(){
        totalRentals--;
    }

    public void incrementLateReturns() {
        lateReturns++;
    }

    public void decrementLateReturns() {
        lateReturns--;
    }
}
