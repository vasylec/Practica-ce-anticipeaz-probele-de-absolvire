package com.example.ui.view;

import com.example.data.entity.PageSize;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.ui.form.AddVehicleForm;
import com.example.ui.form.VehicleForm;
import com.example.ui.dialog.AvailabilityDialog;
import com.example.ui.dialog.ImageDialog;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.VehicleListPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "database/vehicle-list", layout = MainLayout.class)
@PageTitle("Vehicle List")
@RolesAllowed("ADMIN")
public class VehicleList extends AbstractPaginatedListView {
    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class);

    private final VehicleForm vehicleForm;
    private final AddVehicleForm addForm;

    private final VehicleListPresenter presenter;

    private final Button addButton = new Button("Add new vehicle");
    private final Button checkAvailabilityButton = new Button("Check availability");

    public VehicleList(VehicleListPresenter presenter) {
        vehicleForm = new VehicleForm();
        addForm = new AddVehicleForm();

        this.presenter = presenter;

        configureGrid();
        configureForm();
        configureActions();

        vehicleForm.setVisible(false);
        addForm.setVisible(false);

        initializeListView("Search vehicles...", getContent(), checkAvailabilityButton, addButton);
        getStyle().set("overflow", "hidden");
        presenter.setView(this);
    }

    private void configureForm() {
        vehicleForm.setWidth("25em");
        addForm.setWidth("25em");
        vehicleForm.setHeightFull();
        addForm.setHeightFull();
        vehicleForm.getStyle()
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("padding-right", "var(--lumo-space-s)");
        addForm.getStyle()
                .set("overflow-y", "auto")
                .set("overflow-x", "hidden")
                .set("padding-right", "var(--lumo-space-s)");
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, vehicleForm, addForm);
        content.setFlexGrow(1, grid);
        content.setFlexGrow(0, vehicleForm);
        content.setFlexGrow(0, addForm);
        content.setSizeFull();
        content.setMaxHeight("95%");
        content.getStyle().set("min-height", "0");
        content.setPadding(false);
        content.setSpacing(true);

        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setEmptyStateText("No records found.");
        grid.setColumns("manufacturer", "model", "year", "licensePlate", "engineSize", "currentMileage", "pricePerDay");
    }

    private void configureActions() {
        addButton.addClickListener(e -> presenter.onAddVehicleClicked());
        checkAvailabilityButton.addClickListener(e -> presenter.onCheckAvailabilityClicked(grid.asSingleSelect().getValue()));
        grid.asSingleSelect().addValueChangeListener(e -> presenter.onVehicleSelected(e.getValue()));
        grid.addItemDoubleClickListener(e -> ImageDialog.open(e.getItem()));

        vehicleForm.addListener(VehicleForm.SaveEvent.class, e -> presenter.onSaveVehicle(e.getVehicle()));
        vehicleForm.addListener(VehicleForm.DeleteEvent.class, e -> presenter.onDeleteVehicle(e.getVehicle()));
        vehicleForm.addListener(VehicleForm.CloseFormEvent.class, e -> presenter.onCloseVehicleForm());
        addForm.addListener(AddVehicleForm.AddEvent.class, e -> presenter.onAddVehicle(e.getVehicle()));
    }

    public void setVehicles(List<Vehicle> vehicles) {
        grid.setItems(vehicles);
    }

    public void showAddVehicleForm() {
        addForm.setVisible(true);
        vehicleForm.setVisible(false);
    }

    public void showSelectedVehicle(Vehicle selected) {
        addForm.setVisible(false);

        Vehicle current = vehicleForm.getBinder().getBean();
        if (selected.equals(current)) {
            vehicleForm.setVisible(!vehicleForm.isVisible());
            return;
        }

        vehicleForm.setVehicleDetails(selected);
        vehicleForm.setVisible(true);
    }

    public void hideVehicleForm() {
        vehicleForm.setVisible(false);
    }

    public void openAvailabilityDialog(Vehicle vehicle, List<Rental> rentals) {
        AvailabilityDialog.open(vehicle, rentals);
    }

    @Override
    protected void onFilterChanged(String value) {
        presenter.onFilterChanged(value);
    }

    @Override
    protected void onPageSizeChange(PageSize size) {
        presenter.onPageSizeChange(size);
    }

    @Override
    protected void nextPage() {
        presenter.nextPage();
    }

    @Override
    protected void previousPage() {
        presenter.previousPage();
    }

    @Override
    protected void firstPage() {
        presenter.firstPage();
    }

    @Override
    protected void lastPage() {
        presenter.lastPage();
    }

    @Override
    protected void onPageSelectorChange(int number) {
        presenter.onPageSelectorChange(number);
    }
}
