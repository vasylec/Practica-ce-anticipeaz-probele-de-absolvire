package com.example.ui.view;

import com.example.data.entity.Vehicle;
import com.example.ui.dialog.ImageDialog;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.VehiclesTotalRentsListPresenter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "service/rents-per-vehicle", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class VehiclesTotalRentsList extends VerticalLayout {
    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class, false);
    private final VehiclesTotalRentsListPresenter presenter;

    public VehiclesTotalRentsList(VehiclesTotalRentsListPresenter presenter) {
        this.presenter = presenter;

        configureGrid();
        configureActions();
        presenter.setView(this);
        setSizeFull();


        add(
                grid
        );
    }

    public void setVehicles(List<Vehicle> vehicles) {
        grid.setItems(vehicles);
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setEmptyStateText("No records found.");
        grid.setColumns("manufacturer", "model", "year", "licensePlate", "engineSize", "currentMileage", "pricePerDay");
        grid.addColumn(presenter::getTotalRents).setHeader("Total rents").setSortable(true);
    }

    private void configureActions() {
        grid.addItemDoubleClickListener(e -> ImageDialog.open(e.getItem()));
    }
}
