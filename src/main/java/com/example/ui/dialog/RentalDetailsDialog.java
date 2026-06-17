package com.example.ui.dialog;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class RentalDetailsDialog {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    private RentalDetailsDialog() {
    }

    public static void open(Rental rental) {
        if (rental == null) {
            return;
        }

        Vehicle vehicle = rental.getVehicle();
        Customer customer = rental.getCustomer();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rental details");
        dialog.setWidth("min(1100px, 95vw)");
        dialog.setMaxHeight("90vh");

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create(), event -> dialog.close());
        close.addClickShortcut(Key.ESCAPE);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout card = new HorizontalLayout(
                createRentalImagePanel(vehicle),
                createRentalDetailsPanel(rental, vehicle, customer)
        );
        card.setWidthFull();
        card.setPadding(false);
        card.setSpacing(false);
        card.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.STRETCH);
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("overflow", "hidden")
                .set("background", "var(--lumo-base-color)")
                .set("box-shadow", "var(--lumo-box-shadow-m)");

        VerticalLayout layout = new VerticalLayout(close, card);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.END);
        layout.getStyle().set("max-height", "82vh");

        dialog.add(layout);
        dialog.open();
    }

    private static VerticalLayout createRentalImagePanel(Vehicle vehicle) {
        VerticalLayout imagePanel = new VerticalLayout();
        imagePanel.setWidth("50%");
        imagePanel.setMinWidth("0");
        imagePanel.setPadding(false);
        imagePanel.setSpacing(false);
        imagePanel.setAlignItems(FlexComponent.Alignment.CENTER);
        imagePanel.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        imagePanel.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("min-height", "540px");

        if (vehicle == null || vehicle.getImage() == null || vehicle.getImage().isBlank()) {
            Span emptyImage = new Span("This vehicle does not have image.");
            emptyImage.getStyle()
                    .set("color", "var(--lumo-secondary-text-color)")
                    .set("font-size", "18px");
            imagePanel.add(emptyImage);
            return imagePanel;
        }

        ProgressBar loading = new ProgressBar();
        loading.setIndeterminate(true);
        loading.setWidth("220px");

        Span loadingText = new Span("Loading vehicle image...");
        loadingText.getStyle().set("color", "var(--lumo-secondary-text-color)");

        Span errorText = new Span("Vehicle image could not be loaded.");
        errorText.setVisible(false);
        errorText.getStyle()
                .set("color", "var(--lumo-error-text-color)")
                .set("font-size", "16px");

        Image image = new Image(vehicle.getImage(), vehicle.getInfo());
        image.setWidthFull();
        image.setHeight("100%");
        image.getStyle()
                .set("object-fit", "cover")
                .set("display", "none")
                .set("min-height", "540px")
                .set("cursor", "pointer");

        image.getElement().addEventListener("load", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(false);
            image.getStyle().set("display", "block");
        });

        image.getElement().addEventListener("error", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(true);
        });

        image.addClickListener(e -> ImageDialog.open(vehicle.getImage()));

        imagePanel.add(loadingText, loading, errorText, image);
        return imagePanel;
    }

    private static VerticalLayout createRentalDetailsPanel(Rental rental, Vehicle vehicle, Customer customer) {
        VerticalLayout detailsPanel = new VerticalLayout();
        detailsPanel.setWidth("50%");
        detailsPanel.setMinWidth("0");
        detailsPanel.setPadding(true);
        detailsPanel.setSpacing(true);
        detailsPanel.getStyle()
                .set("box-sizing", "border-box")
                .set("overflow", "auto")
                .set("max-height", "72vh");

        H2 title = new H2(vehicle != null ? vehicle.getInfo() : "Rental");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "28px")
                .set("line-height", "1.15");

        Span subtitle = new Span(customer != null ? "Customer: " + customer.getInfo() : "Customer: N/A");
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "15px");

        detailsPanel.add(
                title,
                subtitle,
                createSection("Rental", List.of(
                        detailRow("Rental ID", value(rental.getId())),
                        detailRow("Start date", formatDateTime(rental.getRentalStartDate())),
                        detailRow("End date", formatDateTime(rental.getRentalEndDate())),
                        detailRow("Duration", formatDuration(rental.getRentalStartDate(), rental.getRentalEndDate())),
                        detailRow("Total price", formatMoney(rental.getTotalPrice()))
                )),
                createVehicleSection(vehicle),
                createCustomerSection(customer)
        );

        return detailsPanel;
    }

    private static VerticalLayout createVehicleSection(Vehicle vehicle) {
        if (vehicle == null) {
            return createSection("Vehicle", List.of(detailRow("Vehicle", "N/A")));
        }

        return createSection("Vehicle", List.of(
                detailRow("Vehicle ID", value(vehicle.getId())),
                detailRow("Manufacturer", value(vehicle.getManufacturer())),
                detailRow("Model", value(vehicle.getModel())),
                detailRow("Year", value(vehicle.getYear())),
                detailRow("License plate", value(vehicle.getLicensePlate())),
                detailRow("Current mileage", value(vehicle.getCurrentMileage()) + " km"),
                detailRow("Engine size", value(vehicle.getEngineSize()) + " L"),
                detailRow("Price per day", formatMoney(vehicle.getPricePerDay())),
                detailRow("Image URL", value(vehicle.getImage()))
        ));
    }

    private static VerticalLayout createCustomerSection(Customer customer) {
        if (customer == null) {
            return createSection("Customer", List.of(detailRow("Customer", "N/A")));
        }

        return createSection("Customer", List.of(
                detailRow("Customer ID", value(customer.getId())),
                detailRow("First name", value(customer.getCustomerName())),
                detailRow("Second name", value(customer.getCustomerSecondName())),
                detailRow("Phone", value(customer.getPhone())),
                detailRow("Total rentals", value(customer.getTotalRentals())),
                detailRow("Late returns", value(customer.getLateReturns()))
        ));
    }

    private static VerticalLayout createSection(String title, List<HorizontalLayout> rows) {
        H3 heading = new H3(title);
        heading.getStyle()
                .set("margin", "0 0 8px")
                .set("font-size", "18px");

        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.setWidthFull();
        section.getStyle()
                .set("padding", "16px")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "10px")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("box-sizing", "border-box");

        section.add(heading);
        rows.forEach(section::add);
        return section;
    }

    private static HorizontalLayout detailRow(String label, String value) {
        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-weight", "600")
                .set("color", "var(--lumo-secondary-text-color)");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("text-align", "right")
                .set("overflow-wrap", "anywhere");

        HorizontalLayout row = new HorizontalLayout(labelSpan, valueSpan);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);
        row.getStyle()
                .set("gap", "16px")
                .set("padding", "6px 0")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");
        return row;
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "N/A" : dateTime.format(DATE_TIME_FORMATTER);
    }

    private static String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || end.isBefore(start)) {
            return "N/A";
        }

        long days = Duration.between(start, end).toDays();
        long hours = Duration.between(start, end).minusDays(days).toHours();
        return days + " days, " + hours + " hours";
    }

    private static String formatMoney(BigDecimal value) {
        return value == null ? "N/A" : "$" + value;
    }

    private static String formatMoney(Double value) {
        return value == null ? "N/A" : "$" + String.format("%.2f", value);
    }

    private static String value(Object value) {
        return value == null || value.toString().isBlank() ? "N/A" : value.toString();
    }
}
