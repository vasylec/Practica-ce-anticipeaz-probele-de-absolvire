package com.example.ui.presenter;

import com.example.data.entity.Vehicle;
import com.example.service.RentalService;
import com.example.ui.view.VehiclesTotalRentsList;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class VehiclesTotalRentsListPresenter {

    private VehiclesTotalRentsList view;
    private final RentalService service;

    public VehiclesTotalRentsListPresenter(RentalService service) {
        this.service = service;
    }

    public void setView(VehiclesTotalRentsList view) {
        this.view = view;
        updateList();
    }

    private void updateList() {
        view.setVehicles(service.findAllVehicles());
    }

    public int getTotalRents(Vehicle vehicle) {
        return service.countRentsForVehicle(vehicle);
    }
}
