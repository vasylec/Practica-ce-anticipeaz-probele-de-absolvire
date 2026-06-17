package com.example.ui.view;

import com.example.data.entity.Customer;
import com.example.data.entity.PageSize;
import com.example.ui.form.CustomerForm;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.CustomerListPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "database/customer-list", layout = MainLayout.class)
@PageTitle("Customer List")
@RolesAllowed("ADMIN")
public class CustomerList extends AbstractPaginatedListView {
    private final Grid<Customer> grid = new Grid<>(Customer.class);
    private final CustomerForm form;

    private final CustomerListPresenter presenter;

    public CustomerList(CustomerListPresenter presenter) {
        form = new CustomerForm();

        this.presenter = presenter;

        configureGrid();
        configureForm();
        configureActions();

        initializeListView("Search customers...", getContent());
        presenter.setView(this);
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

    private void configureForm() {
        form.setWidth("25em");
        form.setHeight("0em");
    }

    private void configureGrid() {
        grid.setColumns("customerName", "customerSecondName", "phone", "totalRentals", "lateReturns");
        grid.setSizeFull();
        grid.setEmptyStateText("No records found.");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.addComponentColumn(customer -> {
            String username = presenter.getUsernameForCustomer(customer);

            if (username != null) {
                return new Span(username);
            }

            Span notRegistered = new Span("NOT REGISTERED");
            notRegistered.getStyle()
                    .set("color", "red");
            return notRegistered;
        }).setHeader("In App Username").setAutoWidth(true).setSortable(true);
    }

    private void configureActions() {
        grid.asSingleSelect().addValueChangeListener(e -> presenter.onCustomerSelected(e.getValue()));
        form.addListener(CustomerForm.AddEvent.class, e -> presenter.onAddCustomer(e.getFormData()));
        form.addListener(CustomerForm.UpdateEvent.class, e -> presenter.onUpdateCustomer(e.getCustomer()));
        form.addListener(CustomerForm.DeleteEvent.class, e -> presenter.onDeleteCustomer(e.getCustomer()));
    }

    public void setCustomers(List<Customer> customers) {
        grid.setItems(customers);
    }

    public void showCustomer(Customer customer) {
        form.setCustomerDetails(customer);
    }

    public void showFormError(String message) {
        form.showError(message);
    }

    public void hideForm() {
        form.setVisible(false);
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
