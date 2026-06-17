package com.example.ui.form;

import com.example.data.entity.Vehicle;
import com.example.ui.dialog.ImageDialog;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

import java.math.BigDecimal;

@UIScope
public class AddVehicleForm extends FormLayout {
    private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);

    TextField manufacturer = new TextField("Manufacturer");
    TextField model = new TextField("Model");
    IntegerField year = new IntegerField("Year");
    TextField licensePlate = new TextField("License Plate");
    NumberField pricePerDay = new NumberField("Price Per Day");
    IntegerField currentMileage = new IntegerField("Current Mileage");
    NumberField engineSize = new NumberField("Engine Size");
    TextField imageLink = new TextField("Image Link");
    Span raiseError = new Span("Complete all fields !");
    VerticalLayout imagePreviewLayout = new VerticalLayout();
    Image imagePreview = new Image();
    ProgressBar imageLoading = new ProgressBar();
    Span imageError = new Span("Image link is invalid");
    private boolean imageLinkValid = true;
    private boolean imageLinkLoading;

    Button confirm = new Button("Confirm");
    Button cancel = new Button("Cancel");

    public AddVehicleForm() {
        binder.bindInstanceFields(this);

        HorizontalLayout horizontalLayout = configureTitle();
        configureFields();
        configureImagePreview();
        setLogic();


        add(
                horizontalLayout,
                manufacturer,
                model,
                year,
                licensePlate,
                engineSize,
                currentMileage,
                pricePerDay,
                imageLink,
                imagePreviewLayout,
                raiseError,
                confirm,
                cancel
        );
    }

    private void setLogic() {
        confirm.addClickListener(e -> {
            if (manufacturer.isEmpty()
                    || model.isEmpty()
                    || year.isEmpty()
                    || licensePlate.isEmpty()
                    || pricePerDay.isEmpty()
                    || currentMileage.isEmpty()
                    || engineSize.isEmpty()) {

                raiseError.setText("Please fill all the fields exception is\n image link field");
                raiseError.setVisible(true);
                return;
            }

            if (!isLicensePlateValid()) {
                raiseError.setText("License plate must use format AAA-123");
                raiseError.setVisible(true);
                return;
            }

            if (!imageLink.isEmpty() && !imageLinkValid) {
                raiseError.setText("Please enter a valid image link or leave the image link field empty");
                raiseError.setVisible(true);
                return;
            }

            if (!imageLink.isEmpty() && imageLinkLoading) {
                raiseError.setText("Please wait until the image preview finishes loading");
                raiseError.setVisible(true);
                return;
            }

            Vehicle vehicle = new Vehicle(
                    currentMileage.getValue(),
                    BigDecimal.valueOf(engineSize.getValue()),
                    manufacturer.getValue(),
                    model.getValue(),
                    BigDecimal.valueOf(pricePerDay.getValue()),
                    year.getValue(),
                    licensePlate.getValue().trim().toUpperCase(),
                    imageLink.getValue()
            );
            if (!imageLink.isEmpty()) {
                vehicle.setImage(imageLink.getValue());
            }

            fireEvent(new AddEvent(this, vehicle));
        });

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(e -> {
            setVisible(false);
        });
    }

    private void configureFields() {
        raiseError.setVisible(false);
        raiseError.getStyle().set("color", "red");
        manufacturer.setRequired(true);
        model.setRequired(true);
        year.setRequired(true);
        licensePlate.setRequired(true);
        pricePerDay.setRequired(true);
        currentMileage.setRequired(true);
        engineSize.setRequired(true);
        imageLink.setRequired(false);


        manufacturer.addKeyPressListener(e -> raiseError.setVisible(false));
        model.addKeyPressListener(e -> raiseError.setVisible(false));
        year.addKeyPressListener(e -> raiseError.setVisible(false));
        licensePlate.addKeyPressListener(e -> raiseError.setVisible(false));
        pricePerDay.addKeyPressListener(e -> raiseError.setVisible(false));
        currentMileage.addKeyPressListener(e -> raiseError.setVisible(false));
        engineSize.addKeyPressListener(e -> raiseError.setVisible(false));
        imageLink.addKeyPressListener(e -> raiseError.setVisible(false));
    }

    private void configureImagePreview() {
        imagePreview.setHeight("150px");
        imagePreview.setMaxWidth("100%");
        imagePreview.setAlt("Vehicle image preview");
        imagePreview.getStyle().setBorderRadius("15px");
        imagePreview.getStyle().set("object-fit", "contain");
        imagePreview.getStyle().set("display", "none");
        imagePreview.getStyle().setCursor("pointer");
        imagePreview.addClickListener(e -> {
            if (!imageLink.isEmpty() && imageLinkValid && !imageLinkLoading) {
                ImageDialog.open(imageLink.getValue());
            }
        });

        imageLoading.setIndeterminate(true);
        imageLoading.setWidth("220px");
        imageLoading.setVisible(false);

        imageError.getStyle().set("color", "red");
        imageError.setVisible(false);

        imagePreviewLayout.setPadding(false);
        imagePreviewLayout.setSpacing(false);
        imagePreviewLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        imagePreviewLayout.add(imageLoading, imageError, imagePreview);
        imagePreviewLayout.setVisible(false);

        imagePreview.getElement().addEventListener("load", event -> {
            imageLinkValid = true;
            imageLinkLoading = false;
            imageLoading.setVisible(false);
            imageError.setVisible(false);
            imagePreview.getStyle().set("display", "block");
        });

        imagePreview.getElement().addEventListener("error", event -> {
            imageLinkValid = false;
            imageLinkLoading = false;
            imageLoading.setVisible(false);
            imagePreview.getStyle().set("display", "none");
            imageError.setVisible(true);
        });

        imageLink.addValueChangeListener(event -> updateImagePreview(event.getValue()));
    }

    private void updateImagePreview(String link) {
        imageLinkValid = true;
        imageLinkLoading = false;
        imageError.setVisible(false);
        imagePreview.getStyle().set("display", "none");

        if (link == null || link.isBlank()) {
            imageLoading.setVisible(false);
            imagePreviewLayout.setVisible(false);
            return;
        }

        imagePreviewLayout.setVisible(true);
        imageLoading.setVisible(true);
        imageLinkLoading = true;
        imagePreview.getElement().executeJs("""
                this.removeAttribute('src');
                window.setTimeout(() => {
                    this.src = $0;
                });
                """, link);
    }

    private static HorizontalLayout configureTitle() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H2("Vehicle"));
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        horizontalLayout.setPadding(true);
        return horizontalLayout;
    }

    private boolean isLicensePlateValid() {
        return licensePlate.getValue() != null
                && licensePlate.getValue().trim().toUpperCase().matches("[A-Z]{3}-[0-9]{3}");
    }

    public static class AddEvent extends ComponentEvent<AddVehicleForm> {
        private final Vehicle vehicle;

        public AddEvent(AddVehicleForm source, Vehicle vehicle) {
            super(source, false);
            this.vehicle = vehicle;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener){
        return getEventBus().addListener(eventType,listener);
    }
}
