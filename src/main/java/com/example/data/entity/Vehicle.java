package com.example.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "vehicle",
        indexes = {
                @Index(name = "idx_vehicle_manufacturer", columnList = "manufacturer"),
                @Index(name = "idx_vehicle_model", columnList = "model"),
                @Index(name = "idx_vehicle_license_plate", columnList = "license_plate")
        }
)
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    private int currentMileage;
    @Column(precision = 6, scale = 1)
    private BigDecimal engineSize;
    private String manufacturer;
    private String model;
    @Column(precision = 8, scale = 2)
    private BigDecimal pricePerDay;
    private int manufacturerYear;
    private String licensePlate;

    private String image;

    public Vehicle() {
    }

    public Vehicle(int currentMileage, BigDecimal engineSize, String manufacturer, String model, BigDecimal pricePerDay, int year, String image) {
        this(currentMileage, engineSize, manufacturer, model, pricePerDay, year, null, image);
    }

    public Vehicle(int currentMileage, BigDecimal engineSize, String manufacturer, String model, BigDecimal pricePerDay, int year, String licensePlate, String image) {
        this.currentMileage = currentMileage;
        this.engineSize = engineSize;
        this.manufacturer = manufacturer;
        this.model = model;
        this.pricePerDay = pricePerDay;
        this.manufacturerYear = year;
        this.licensePlate = licensePlate;
        this.image = image;
    }

    public String getInfo(){
        return manufacturer + "  " + model + "  " + manufacturerYear;
    }

    public Long getId() {
        return vehicleId;
    }

    public void setId(Long id) {
        this.vehicleId = id;
    }

    public int getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(int currentMileage) {
        this.currentMileage = currentMileage;
    }

    public Double getEngineSize() {
        return engineSize.doubleValue();
    }

    public void setEngineSize(Double engine) {
        engineSize = BigDecimal.valueOf(engine);
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getPricePerDay() {
        return pricePerDay.doubleValue();
    }

    public void setPricePerDay(Double price) {
        pricePerDay = BigDecimal.valueOf(price);
    }

    public int getYear() {
        return manufacturerYear;
    }

    public void setYear(int year) {
        this.manufacturerYear = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
