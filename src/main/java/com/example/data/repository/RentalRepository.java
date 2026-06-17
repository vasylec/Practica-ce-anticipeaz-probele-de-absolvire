package com.example.data.repository;

import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> , JpaSpecificationExecutor<Rental> {
    List<Rental> findByVehicle(Vehicle vehicle);

    Optional<Rental> findFirstByVehicleAndRentalStartDateBeforeAndRentalEndDateAfterOrderByRentalEndDateDesc(
            Vehicle vehicle,
            LocalDateTime requestedEndDate,
            LocalDateTime requestedStartDate
    );

    @Query("""
        SELECT v.manufacturer, v.model, COUNT(r)
        FROM Rental r
        JOIN r.vehicle v
        GROUP BY v.manufacturer, v.model
""")
    List<Object[]> getVehicleRentalsForChart();

    @Query("""
    SELECT
        MONTH(r.rentalStartDate),
        YEAR(r.rentalStartDate),
        COUNT(r)
    FROM Rental r
    GROUP BY
        YEAR(r.rentalStartDate),
        MONTH(r.rentalStartDate)
    ORDER BY YEAR(r.rentalStartDate), MONTH(r.rentalStartDate)
""")
    List<Object[]> getTotalRentsPerMonth();

    @Query("""
        SELECT
        MONTH(rentalStartDate),
        YEAR(rentalStartDate),
        SUM(totalPrice)
    FROM Rental
    GROUP BY
        YEAR(rentalStartDate),
        MONTH(rentalStartDate)
    ORDER BY YEAR(rentalStartDate), MONTH(rentalStartDate)
""")
    List<Object[]> getRevenuePerMonth();

    @Query("""
    SELECT v FROM Vehicle v
    WHERE v.vehicleId NOT IN (
        SELECT r.vehicle.vehicleId
        FROM Rental r
        WHERE CURRENT_TIMESTAMP BETWEEN r.rentalStartDate AND r.rentalEndDate
    )
""")
    List<Vehicle> findAvailableVehicles();

    int countRentalByVehicle(Vehicle vehicle);
}
