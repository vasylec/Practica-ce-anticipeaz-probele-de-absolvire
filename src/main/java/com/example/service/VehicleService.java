package com.example.service;

import com.example.data.entity.Vehicle;
import com.example.data.repository.VehicleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> search(String filter) {
        if (filter == null || filter.isBlank()) {
            return findAll();
        }

        return vehicleRepository.searchVehicles(filter.trim());
    }

    public void updateVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public int deleteVehicle(Vehicle vehicle) {
        try{
            vehicleRepository.delete(vehicle);
            return 0;
        }
        catch (DataIntegrityViolationException e) {
            return 1;
        }
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public Page<Vehicle> getVehiclesPage(int page, int size, String filter){
        Pageable pageable = PageRequest.of(page, size);
        return vehicleRepository.searchVehicles(filter, pageable);
    }

    public int getTotalPagesNumber(int pageSize, String filterText){
        Long totalRecords = 0L;

        if(filterText == null || filterText.isBlank()){
            totalRecords = vehicleRepository.count();
        }
        else{
            totalRecords = vehicleRepository.count(filterText);
        }

        if(totalRecords % pageSize == 0){
            return (int) (totalRecords / pageSize);
        }
        else{
            return (int) (totalRecords / pageSize) + 1;
        }
    }
}
