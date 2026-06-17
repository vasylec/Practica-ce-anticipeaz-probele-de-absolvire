package com.example.data.repository;

import com.example.data.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("""
            SELECT v FROM Vehicle v
            WHERE LOWER(v.manufacturer) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.model) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    List<Vehicle> searchVehicles(@Param("filter") String filter);

    @Query("""
            SELECT v FROM Vehicle v
            WHERE LOWER(v.manufacturer) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.model) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    Page<Vehicle> searchVehicles(
            @Param("filter") String filter,
            Pageable pageable
    );

    @Query("""
            SELECT COUNT(v) FROM Vehicle v
            WHERE LOWER(v.manufacturer) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.model) LIKE LOWER(CONCAT('%', :filter, '%'))
               OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :filter, '%'))
            """)
    Long count(@Param("filter") String filter);
}
