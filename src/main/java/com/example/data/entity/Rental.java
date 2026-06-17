package com.example.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "rental",
        indexes = {
                @Index(name = "idx_rental_customer", columnList = "customer_id"),
                @Index(name = "idx_rental_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_rental_dates", columnList = "rental_start_date,rental_end_date")
        }
)
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    private LocalDateTime rentalStartDate;
    private LocalDateTime rentalEndDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public Rental() {
    }

    public Rental(Customer customer, Vehicle vehicle, LocalDateTime rentalStartDate, LocalDateTime rentalEndDate, BigDecimal totalPrice) {
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.totalPrice = totalPrice;
    }

    public String getInfo(){
        return vehicle.getInfo() + " " + customer.getInfo();
    }

    public LocalDateTime getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(LocalDateTime rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public LocalDateTime getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(LocalDateTime rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setId(Long id) {
        this.rentalId = id;
    }

    public Long getId() {
        return rentalId;
    }

}
