package com.example.ui.form;

import com.example.data.entity.Vehicle;
import com.example.ui.dialog.ConfirmDialogs;
import com.example.ui.dialog.ImageDialog;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertyId;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
public class VehicleForm extends FormLayout {
    private final Binder<Vehicle> binder = new BeanValidationBinder<>(Vehicle.class);

    TextField manufacturer = new TextField("Manufacturer");
    TextField model = new TextField("Model");
    IntegerField year = new IntegerField("Year");
    TextField licensePlate = new TextField("License Plate");
    NumberField pricePerDay = new NumberField("Price Per Day");
    IntegerField currentMileage = new IntegerField("Current Mileage");
    NumberField engineSize = new NumberField("Engine Size");
    @PropertyId("image")
    TextField imageLink = new TextField("Image Link");

    VerticalLayout imageContainer = new VerticalLayout();
    Image image = new Image();
    ProgressBar loading = new ProgressBar();
    Span loadingText = new Span("Loading image...");
    Span errorText = new Span("Image could not be loaded.");
    private boolean imageLinkValid = true;
    private boolean imageLinkLoading;
    private boolean skipImagePreviewLoading;

    Button delete = new Button("Delete");
    Button update = new Button("Update");
    Button cancel = new Button("Cancel");

    public VehicleForm() {
        binder.bindInstanceFields(this);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new H2("Vehicle"));
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        horizontalLayout.setPadding(true);

        configureImageContainer();

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
                imageContainer,
                buttonLayout()
        );
    }

    private void configureImageContainer() {
        image.setHeight("150px");
        image.setSizeFull();
        image.setAlt("Vehicle Image");
        image.getStyle().setBorderRadius("15px");
        image.getStyle().setBorder("1px solid black");
        image.getStyle().set("display", "none");
        image.getStyle().setCursor("pointer");
        image.addClickListener(e -> {
            ImageDialog.open(binder.getBean());
        });

        imageContainer.setHeight("100px");
        imageContainer.setWidth("100%");
        imageContainer.setPadding(false);
        imageContainer.setSpacing(false);
        imageContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        imageContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        imageContainer.add(loadingText, loading, errorText, image);
        imageContainer.setVisible(false);

        loading.setIndeterminate(true);
        loading.setWidth("220px");

        loadingText.setVisible(false);
        loading.setVisible(false);
        errorText.setVisible(false);

        image.getElement().addEventListener("error", event -> {
            imageLinkValid = false;
            imageLinkLoading = false;
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(true);
            image.getStyle().set("display", "none");
        });

        image.getElement().addEventListener("load", event -> {
            imageLinkValid = true;
            imageLinkLoading = false;
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(false);
            image.getStyle().set("display", "block");
            imageContainer.setHeight(null);
        });

        imageLink.addValueChangeListener(event -> {
            if (!skipImagePreviewLoading) {
                loadImagePreview(event.getValue());
            }
        });
    }

    public void loadImagePreview(String imageUrl) {
        imageLinkValid = true;
        imageLinkLoading = false;
        errorText.setVisible(false);
        image.getStyle().set("display", "none");

        if (imageUrl == null || imageUrl.isBlank()) {
            loadingText.setVisible(false);
            loading.setVisible(false);
            imageContainer.setVisible(false);
            imageContainer.setHeight("100px");
            return;
        }

        imageContainer.setVisible(true);
        loadingText.setVisible(true);
        loading.setVisible(true);
        loading.setIndeterminate(true);
        errorText.setVisible(false);
        image.getStyle().set("display", "none");
        imageContainer.setHeight("150px");
        imageLinkLoading = true;

        image.getElement().executeJs("""
                this.removeAttribute('src');
                window.setTimeout(() => {
                    this.src = $0;
                });
                """, imageUrl);
    }

    private Component buttonLayout() {
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        update.addClickListener(e -> {
            Vehicle vehicle = binder.getBean();
            if(vehicle != null){
                if (!imageLink.isEmpty() && imageLinkLoading) {
                    Notification.show("Please wait until the image preview finishes loading !")
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!imageLink.isEmpty() && !imageLinkValid) {
                    Notification.show("Image link is invalid !")
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!isLicensePlateValid()) {
                    Notification.show("License plate must use format AAA-123 !")
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                licensePlate.setValue(licensePlate.getValue().trim().toUpperCase());
                binder.writeBeanIfValid(vehicle);
                if (imageLink.isEmpty()) {
                    vehicle.setImage(null);
                }
                fireEvent(new SaveEvent(this, vehicle));
            }
        });

        delete.addClickListener(e -> {
            Vehicle vehicle = binder.getBean();

            if(vehicle != null){
                ConfirmDialogs.deleteDialog(
                        "Delete Vehicle",
                        "Are you sure you want to delete this vehicle: "+ vehicle.getInfo() +" ?",
                        event -> {
                            binder.writeBeanIfValid(vehicle);

                            fireEvent(new DeleteEvent(this, vehicle));
                        });
            }
        });

        cancel.addClickListener(e -> {
            fireEvent(new CloseFormEvent(this));
        });

        cancel.addClickShortcut(Key.ESCAPE);
        HorizontalLayout layout = new HorizontalLayout(update, delete, cancel);
        layout.setFlexGrow(1, update, delete, cancel);
        return layout;
    }

    private boolean isLicensePlateValid() {
        return licensePlate.getValue() != null
                && licensePlate.getValue().trim().toUpperCase().matches("[A-Z]{3}-[0-9]{3}");
    }

    public void setVehicleDetails(Vehicle vehicle){
        skipImagePreviewLoading = true;
        binder.setBean(null);
        binder.setBean(vehicle);
        skipImagePreviewLoading = false;
        loadImagePreview(vehicle.getImage());
    }

    public static class SaveEvent extends ComponentEvent<VehicleForm> {
        private final Vehicle vehicle;

        public SaveEvent(VehicleForm source, Vehicle vehicle) {
            super(source, false);
            this.vehicle = vehicle;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }
    }

    public static class DeleteEvent extends ComponentEvent<VehicleForm> {
        private final Vehicle vehicle;

        public DeleteEvent(VehicleForm source, Vehicle vehicle) {
            super(source, false);
            this.vehicle = vehicle;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }
    }

    public static class CloseFormEvent extends ComponentEvent<VehicleForm> {
        public CloseFormEvent(VehicleForm source) {
            super(source, false);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener){
        return getEventBus().addListener(eventType,listener);
    }

    public Binder<Vehicle> getBinder() {
        return binder;
    }
}
