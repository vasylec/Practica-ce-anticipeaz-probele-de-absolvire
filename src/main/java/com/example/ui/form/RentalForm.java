package com.example.ui.form;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.ui.dialog.ConfirmDialogs;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UIScope
public class RentalForm extends FormLayout {

    private final Binder<Rental> binder = new BeanValidationBinder<>(Rental.class);

    DateTimePicker rentalStartDate = new DateTimePicker("Rental Start Date");
    DateTimePicker rentalEndDate = new DateTimePicker("Rental End Date");
    NumberField totalPrice = new NumberField("Total Price");
    ComboBox<Vehicle> vehicle = new  ComboBox<>("Vehicle");
    ComboBox<Customer> customer = new  ComboBox<>("Customer");
    Span errorMessage = new Span();

    Button add = new Button("Add");
    Button delete = new Button("Delete");
    Button update = new Button("Update");
    Button clear = new Button("Clear");

    public RentalForm() {
        binder.bindInstanceFields(this);

        configureFields();

        add(
                vehicle,
                customer,
                rentalStartDate,
                rentalEndDate,
                totalPrice,
                errorMessage,
                buttonLayout()
        );
    }

    private void configureFields() {
        errorMessage.setVisible(false);
        errorMessage.setText("Please select a rental form the list !");
        errorMessage.getStyle().set("color", "red");

        vehicle.addFocusListener(e -> errorMessage.setVisible(false));
        customer.addFocusListener(e -> errorMessage.setVisible(false));
        rentalStartDate.addFocusListener(e -> errorMessage.setVisible(false));
        rentalEndDate.addFocusListener(e -> errorMessage.setVisible(false));
        totalPrice.addKeyPressListener(e -> errorMessage.setVisible(false));
        totalPrice.addValueChangeListener(e -> {
            errorMessage.setVisible(false);
        });


        rentalEndDate.addValueChangeListener(e -> {
            Vehicle v = vehicle.getValue();

            var start = rentalStartDate.getValue();
            var end = rentalEndDate.getValue();

            if (start == null || end == null) {
                totalPrice.setPlaceholder(null);
                return;
            }

            if (!end.isAfter(start)) {
                totalPrice.setPlaceholder(null);
                return;
            }

            long days = ChronoUnit.DAYS.between(start, end);

            if (v != null) {
                totalPrice.setPlaceholder(v.getPricePerDay() * days + "") ;
            } else {
                totalPrice.setPlaceholder(null) ;
            }
        });
    }

    public ComboBox<Vehicle> getVehicle() {
        return vehicle;
    }

    public ComboBox<Customer> getCustomer() {
        return customer;
    }

    private Component buttonLayout() {
        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        clear.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        add.addClickListener(e -> {
            Customer selectedCustomer = customer.getValue();
            Vehicle selectedVehicle = vehicle.getValue();
            LocalDateTime startDate = rentalStartDate.getValue();
            LocalDateTime endDate = rentalEndDate.getValue();
            Double price = totalPrice.getValue();

            if(selectedCustomer == null || selectedVehicle == null || startDate == null || endDate == null || price == null) {
                errorMessage.setText("Please complete all fields !");
                errorMessage.setVisible(true);
            }
            else{
                if(endDate.isBefore(startDate)) {
                    errorMessage.setText("End Date must be after Start Date !");
                    errorMessage.setVisible(true);
                    return;
                }

                Rental rental = new Rental(
                        selectedCustomer,
                        selectedVehicle,
                        startDate,
                        endDate,
                        BigDecimal.valueOf(price)
                );


                fireEvent(new RentalForm.AddEvent(this, rental));
                errorMessage.setVisible(false);
            }
        });

        update.addClickListener(e -> {
            Rental rental = binder.getBean();

            if(rental != null){
                binder.writeBeanIfValid(rental);
                fireEvent(new RentalForm.SaveEvent(this, rental));
                errorMessage.setVisible(false);
                Notification.show("Rental successfully updated !").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
            else{
                errorMessage.setText("Please select a rental from the list !");
                errorMessage.setVisible(true);
            }
        });

        delete.addClickListener(e -> {
            Rental rental = binder.getBean();

            if(rental != null){

                ConfirmDialogs.deleteDialog(
                        "Delete Rental",
                        "Are you sure you want to delete this rental: "+ rental.getInfo() +" ?",
                        event -> {
                            binder.writeBeanIfValid(rental);
                            fireEvent(new RentalForm.DeleteEvent(this, rental));
                            errorMessage.setVisible(false);
                            Notification.show("Rental successfully deleted !").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        }
                );
            }
            else{
                errorMessage.setText("Please select a rental from the list !");
                errorMessage.setVisible(true);
            }
        });

        clear.addClickListener(e -> {
            errorMessage.setVisible(false);
            fireEvent(new RentalForm.ClearFormEvent(this));
        });

        HorizontalLayout layout = new HorizontalLayout(add, update, delete, clear);
        layout.setFlexGrow(1, add, update, delete, clear);
        return layout;
    }

    public void setRentalDetails(Rental rental){
        binder.setBean(null);
        binder.setBean(rental);
    }

    public Binder<Rental> getBinder() {
        return binder;
    }

    public static class AddEvent extends ComponentEvent<RentalForm> {
        private final Rental rental;

        public AddEvent(RentalForm source, Rental rental) {
            super(source, false);
            this.rental = rental;
        }

        public Rental getRental() {
            return rental;
        }
    }

    public static class SaveEvent extends ComponentEvent<RentalForm> {
        private final Rental rental;

        public SaveEvent(RentalForm source, Rental rental) {
            super(source, false);
            this.rental = rental;
        }

        public Rental getRental() {
            return rental;
        }
    }

    public static class DeleteEvent extends ComponentEvent<RentalForm> {
        private final Rental rental;

        public DeleteEvent(RentalForm source, Rental rental) {
            super(source, false);
            this.rental = rental;
        }

        public Rental getRental() {
            return rental;
        }
    }

    public static class ClearFormEvent extends ComponentEvent<RentalForm> {
        public ClearFormEvent(RentalForm source) {
            super(source, false);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener){
        return getEventBus().addListener(eventType,listener);
    }
}
