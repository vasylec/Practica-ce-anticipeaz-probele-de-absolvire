package com.example.ui.form;

import com.example.data.entity.Customer;
import com.example.ui.dialog.ConfirmDialogs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
public class CustomerForm extends FormLayout {

    private final Binder<Customer> binder = new BeanValidationBinder<>(Customer.class);

    private final TextField customerName = new TextField("First Name");
    private final TextField customerSecondName = new TextField("Second Name");
    private final TextField phone = new TextField("Phone");
    private final IntegerField totalRentals = new IntegerField("Total Rentals");
    private final IntegerField lateReturns = new IntegerField("Late Returns");
    private final Span errorMessage = new Span();

    private final Button add = new Button("Add");
    private final Button delete = new Button("Delete");
    private final Button update = new Button("Update");

    public CustomerForm() {
        binder.bindInstanceFields(this);
        errorMessage.getStyle().set("color", "red");

        configureFields();

        add(customerName, customerSecondName, phone, totalRentals, lateReturns, errorMessage, buttonLayout());
    }

    private void configureFields() {
        errorMessage.setVisible(false);
        customerName.addValueChangeListener(e -> hideError());
        customerSecondName.addValueChangeListener(e -> hideError());
        phone.addValueChangeListener(e -> hideError());
        totalRentals.addValueChangeListener(e -> hideError());
        lateReturns.addValueChangeListener(e -> hideError());
    }

    private Component buttonLayout() {
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

        add.addClickListener(e -> {
            hideError();
            fireEvent(new AddEvent(this, readFormData()));
        });

        update.addClickListener(e -> {
            Customer customer = binder.getBean();

            if (customer == null) {
                showError("Please select a customer from the list !");
                return;
            }

            if (binder.writeBeanIfValid(customer)) {
                hideError();
                fireEvent(new UpdateEvent(this, customer));
            }
            else {
                showError("Please fill all the fields !");
            }
        });

        delete.addClickListener(e -> {
            Customer customer = binder.getBean();

            if (customer != null) {
                ConfirmDialogs.deleteDialog(
                        "Delete Customer",
                        "Are you sure you want to delete this customer: " + customer.getInfo() + " ?",
                        event -> {
                            hideError();
                            fireEvent(new DeleteEvent(this, customer));
                        }
                );
            }
            else {
                showError("Please select a customer from the list !");
            }
        });

        HorizontalLayout layout = new HorizontalLayout(add, update, delete);
        layout.setFlexGrow(1, add, update, delete);
        return layout;
    }

    public void setCustomerDetails(Customer customer) {
        hideError();
        binder.setBean(customer);
    }

    public void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }

    private void hideError() {
        errorMessage.setVisible(false);
    }

    private FormData readFormData() {
        return new FormData(
                customerName.getValue(),
                customerSecondName.getValue(),
                totalRentals.getValue(),
                lateReturns.getValue(),
                phone.getValue()
        );
    }

    public record FormData(
            String customerName,
            String customerSecondName,
            Integer totalRentals,
            Integer lateReturns,
            String phone
    ) {
        public Customer toCustomer() {
            return new Customer(customerName, customerSecondName, totalRentals, lateReturns, phone);
        }
    }

    public static class AddEvent extends ComponentEvent<CustomerForm> {
        private final FormData formData;

        public AddEvent(CustomerForm source, FormData formData) {
            super(source, false);
            this.formData = formData;
        }

        public FormData getFormData() {
            return formData;
        }
    }

    public static class UpdateEvent extends ComponentEvent<CustomerForm> {
        private final Customer customer;

        public UpdateEvent(CustomerForm source, Customer customer) {
            super(source, false);
            this.customer = customer;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    public static class DeleteEvent extends ComponentEvent<CustomerForm> {
        private final Customer customer;

        public DeleteEvent(CustomerForm source, Customer customer) {
            super(source, false);
            this.customer = customer;
        }

        public Customer getCustomer() {
            return customer;
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener){
        return getEventBus().addListener(eventType,listener);
    }
}
