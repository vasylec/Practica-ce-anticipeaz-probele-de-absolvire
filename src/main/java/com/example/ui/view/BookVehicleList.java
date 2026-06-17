package com.example.ui.view;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.ui.CustomNotification;
import com.example.ui.dialog.AvailabilityDialog;
import com.example.ui.dialog.BookingDialog;
import com.example.ui.dialog.ImageDialog;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.BookVehicleListPresenter;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "service/book-vehicle", layout = MainLayout.class)
@PageTitle("Book a Vehicle")
@RolesAllowed({"ADMIN", "USER"})
public class BookVehicleList extends VerticalLayout {
    private final Grid<Vehicle> grid = new Grid<>(Vehicle.class);
    private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);
    private final Button makeRentalButton = new Button("Make new Rental");
    private final Button checkAvailabilityButton = new Button("Check availability");
    private final BookVehicleListPresenter presenter;
    private final Checkbox showOnlyFreeVehicles = new Checkbox("Show only free vehicles");

    private Dialog dialog;

    public BookVehicleList(BookVehicleListPresenter presenter) {
        this.presenter = presenter;

        configureGrid();
        configureActions();
        configureCheckBox();

        setSizeFull();

        add(
                new VerticalLayout(
                        new HorizontalLayout(makeRentalButton, checkAvailabilityButton),
                        showOnlyFreeVehicles
                ),
                grid
        );

        presenter.setView(this);
    }

    private void configureCheckBox() {
        showOnlyFreeVehicles.getStyle().setMarginTop("10px");
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setEmptyStateText("No records found.");
        grid.setColumns("manufacturer", "model", "year", "licensePlate", "engineSize", "currentMileage", "pricePerDay");
        grid.asSingleSelect().addValueChangeListener(gridEvent -> binder.setBean(gridEvent.getValue()));
    }

    private void configureActions() {
        makeRentalButton.addClickListener(e -> presenter.onMakeRentalClicked(binder.getBean()));
        checkAvailabilityButton.addClickListener(e -> presenter.onCheckAvailabilityClicked(binder.getBean()));
        grid.addItemDoubleClickListener(e -> ImageDialog.open(e.getItem()));
        showOnlyFreeVehicles.addValueChangeListener(e -> presenter.handleShowOnlyFreeVehicles(e.getValue()));
    }

    public void openRentalDialog(Vehicle vehicle, boolean forAdmin, List<Customer> customers, List<Rental> rentals) {
        dialog = BookingDialog.open(
                vehicle,
                forAdmin,
                customers,
                rentals,
                this::showError,
                presenter::onConfirmRental
        );
    }

    public void openAvailabilityDialog(Vehicle vehicle, List<Rental> rentals) {
        AvailabilityDialog.open(vehicle, rentals);
    }

    public void showError(String message) {
        CustomNotification.showErrorMessage(message);
    }

    public void showSuccess(String message) {
        CustomNotification.showSuccessMessage(message);
    }

    public void refreshBalance() {
        fireEvent(new RefreshBalance(this));
    }

    public void closeDialog() {
        dialog.close();
    }

    public void setVehicles(List<Vehicle> vehicles) {
        grid.setItems(vehicles);
    }

    public static class RefreshBalance extends ComponentEvent<BookVehicleList> {
        public RefreshBalance(BookVehicleList source) {
            super(source, false);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
