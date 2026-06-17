package com.example.ui.view;

import com.example.data.entity.Customer;
import com.example.data.entity.PageSize;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.ui.form.RentalForm;
import com.example.ui.dialog.RentalDetailsDialog;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.RentalListPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "database/rental-list", layout = MainLayout.class)
@PageTitle("Rental List")
@RolesAllowed("ADMIN")
public class RentalList extends AbstractPaginatedListView {
    private final Grid<Rental> grid = new Grid<>(Rental.class);
    private final RentalForm form;
    private final RentalListPresenter presenter;

    public RentalList(RentalListPresenter presenter) {
        this.presenter = presenter;

        form = new RentalForm();
        configureGrid();
        configureForm();
        configureActions();

        initializeListView("Search rentals...", getContent());
        presenter.setView(this);
    }

    private void configureForm() {
        form.setWidth("40em");
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout();
        content.setSizeFull();
        content.add(grid, form);
        content.setMaxHeight("95%");
        content.getStyle().set("min-height", "0");
        content.setPadding(false);
        content.setSpacing(true);
        return content;
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.setEmptyStateText("No records found.");
        grid.removeAllColumns();
        grid.addColumn(rental ->
                rental.getVehicle() != null ? rental.getVehicle().getInfo() : "N/A"
        ).setHeader("Vehicle").setSortable(true);
        grid.addColumn(rental ->
                rental.getCustomer() != null ? rental.getCustomer().getInfo() : "N/A"
        ).setHeader("Customer").setSortable(true);
        grid.addColumn("rentalStartDate");
        grid.addColumn("rentalEndDate");
        grid.addColumn("totalPrice");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void configureActions() {
        grid.asSingleSelect().addValueChangeListener(e -> presenter.onRentalSelected(e.getValue()));
        grid.addItemDoubleClickListener(e -> {
            Rental rental = e.getItem();
            if (rental != null) {
                RentalDetailsDialog.open(rental);
            }
        });

        form.addListener(RentalForm.AddEvent.class, e -> presenter.onAddRental(e.getRental()));
        form.addListener(RentalForm.SaveEvent.class, e -> presenter.onSaveRental(e.getRental()));
        form.addListener(RentalForm.DeleteEvent.class, e -> presenter.onDeleteRental(e.getRental()));
        form.addListener(RentalForm.ClearFormEvent.class, e -> presenter.onClearForm());
    }

    public void setRentals(List<Rental> rentals) {
        grid.setItems(rentals);
    }

    public void setVehicles(List<Vehicle> vehicles) {
        form.getVehicle().setItems(vehicles);
        form.getVehicle().setItemLabelGenerator(Vehicle::getInfo);
    }

    public void setCustomers(List<Customer> customers) {
        form.getCustomer().setItems(customers);
        form.getCustomer().setItemLabelGenerator(Customer::getInfo);
    }

    public void showRental(Rental rental) {
        form.setRentalDetails(rental);
    }

    public void clearSelection() {
        grid.deselectAll();
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
    protected void onPageSelectorChange(int number){
        presenter.onPageSelectorChange(number);
    }
}
