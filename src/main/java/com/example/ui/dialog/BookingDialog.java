package com.example.ui.dialog;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public final class BookingDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private BookingDialog() {
    }

    public static Dialog open(
            Vehicle vehicle,
            boolean forAdmin,
            List<Customer> customers,
            List<Rental> rentals,
            Consumer<String> errorHandler,
            RentalConfirmationHandler confirmationHandler
    ) {
        Dialog dialog = new Dialog();

        Span title = new Span("Select the rental period for " + vehicle.getInfo());
        title.addClassName("booking-dialog-title");
        Span selectedPeriod = new Span("Click a start date, then an end date.");
        Span totalPrice = new Span("Total price: $0.0");

        ComboBox<Customer> customerComboBox = new ComboBox<>();
        customerComboBox.setItems(customers);
        customerComboBox.setItemLabelGenerator(Customer::getInfo);

        Button confirm = new Button("Confirm");
        confirm.setEnabled(false);
        Button cancel = new Button("Cancel");
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Set<LocalDate> unavailableDates = getUnavailableDates(rentals);
        RentalDateRange selectedRange = new RentalDateRange();
        Div calendar = new Div();
        Span monthTitle = new Span();
        AtomicReference<YearMonth> displayedMonth = new AtomicReference<>(YearMonth.now());
        configureBookingCalendarGrid(calendar);

        Runnable updateSummary = () -> updateRentalSummary(vehicle, selectedRange, selectedPeriod, totalPrice, confirm);
        Button previousYear = new Button("<<");
        Button previous = new Button(VaadinIcon.ANGLE_LEFT.create());
        Button next = new Button(VaadinIcon.ANGLE_RIGHT.create());
        Button nextYear = new Button(">>");

        previousYear.addClickListener(e -> {
            displayedMonth.set(displayedMonth.get().minusYears(1));
            renderBookingCalendar(calendar, monthTitle, displayedMonth.get(), unavailableDates, selectedRange, updateSummary, errorHandler);
        });
        previous.addClickListener(e -> {
            displayedMonth.set(displayedMonth.get().minusMonths(1));
            renderBookingCalendar(calendar, monthTitle, displayedMonth.get(), unavailableDates, selectedRange, updateSummary, errorHandler);
        });
        next.addClickListener(e -> {
            displayedMonth.set(displayedMonth.get().plusMonths(1));
            renderBookingCalendar(calendar, monthTitle, displayedMonth.get(), unavailableDates, selectedRange, updateSummary, errorHandler);
        });
        nextYear.addClickListener(e -> {
            displayedMonth.set(displayedMonth.get().plusYears(1));
            renderBookingCalendar(calendar, monthTitle, displayedMonth.get(), unavailableDates, selectedRange, updateSummary, errorHandler);
        });

        HorizontalLayout calendarNavigation = new HorizontalLayout(previousYear, previous, monthTitle, next, nextYear);
        calendarNavigation.setWidthFull();
        calendarNavigation.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        calendarNavigation.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span legend = new Span("Booked days are red and cannot be selected.");
        legend.addClassName("dialog-calendar-legend");
        renderBookingCalendar(calendar, monthTitle, displayedMonth.get(), unavailableDates, selectedRange, updateSummary, errorHandler);

        confirm.addClickListener(e -> confirmationHandler.confirm(
                vehicle,
                selectedRange.getStartDate(),
                selectedRange.getEndDate(),
                customerComboBox.getValue()
        ));
        cancel.addClickListener(e -> dialog.close());

        HorizontalLayout customerLayout = new HorizontalLayout();
        if (forAdmin) {
            customerLayout.setWidthFull();
            customerLayout.setSpacing(true);
            customerLayout.setPadding(true);
            customerLayout.add(new Span("Select Customer: "), customerComboBox);
        }

        VerticalLayout layout = new VerticalLayout(
                title,
                createVehicleImagePreview(vehicle),
                customerLayout,
                selectedPeriod,
                calendarNavigation,
                calendar,
                legend,
                totalPrice,
                new HorizontalLayout(confirm, cancel)
        );
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        dialog.add(layout);
        dialog.open();
        return dialog;
    }

    private static Component createVehicleImagePreview(Vehicle vehicle) {
        Image image = new Image();
        image.setHeight("180px");
        image.setMaxWidth("100%");
        image.setAlt("Vehicle image");
        image.addClassNames("booking-dialog-vehicle-image", "booking-dialog-vehicle-image-hidden");
        image.addClickListener(e -> ImageDialog.open(vehicle));

        Span loadingText = new Span("Loading image...");
        Span errorText = new Span("Image could not be loaded.");
        errorText.setVisible(false);

        ProgressBar loading = new ProgressBar();
        loading.setIndeterminate(true);
        loading.setWidth("220px");

        VerticalLayout imageContainer = new VerticalLayout();
        imageContainer.setWidth("100%");
        imageContainer.setHeight("180px");
        imageContainer.setPadding(false);
        imageContainer.setSpacing(false);
        imageContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        imageContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        imageContainer.add(loadingText, loading, errorText, image);

        image.getElement().addEventListener("load", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(false);
            image.removeClassName("booking-dialog-vehicle-image-hidden");
            imageContainer.setHeight(null);
        });

        image.getElement().addEventListener("error", event -> {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setVisible(true);
            image.addClassName("booking-dialog-vehicle-image-hidden");
        });

        String imageUrl = vehicle.getImage();
        if (imageUrl == null || imageUrl.isBlank()) {
            loadingText.setVisible(false);
            loading.setVisible(false);
            errorText.setText("This vehicle does not have image.");
            errorText.setVisible(true);
            return imageContainer;
        }

        image.getElement().executeJs("""
                this.removeAttribute('src');
                window.setTimeout(() => {
                    this.src = $0;
                });
                """, imageUrl);

        return imageContainer;
    }

    private static Set<LocalDate> getUnavailableDates(List<Rental> rentals) {
        Set<LocalDate> unavailableDates = new HashSet<>();

        rentals.forEach(rental -> {
            LocalDate start = rental.getRentalStartDate().toLocalDate();
            LocalDate end = rental.getRentalEndDate().toLocalDate();

            for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
                unavailableDates.add(date);
            }
        });

        return unavailableDates;
    }

    private static void configureBookingCalendarGrid(Div calendar) {
        calendar.addClassName("dialog-calendar-grid");
    }

    private static void renderBookingCalendar(
            Div calendar,
            Span monthTitle,
            YearMonth month,
            Set<LocalDate> unavailableDates,
            RentalDateRange selectedRange,
            Runnable updateSummary,
            Consumer<String> errorHandler
    ) {
        calendar.removeAll();
        monthTitle.setText(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        addWeekdayHeaders(calendar);
        addBlankDays(calendar, month);
        addMonthDays(calendar, monthTitle, month, unavailableDates, selectedRange, updateSummary, errorHandler);
    }

    private static void addWeekdayHeaders(Div calendar) {
        List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                .forEach(day -> {
                    Span header = new Span(day);
                    header.addClassName("dialog-calendar-weekday");
                    calendar.add(header);
                });
    }

    private static void addBlankDays(Div calendar, YearMonth month) {
        int blanks = month.atDay(1).getDayOfWeek().getValue() - 1;
        for (int i = 0; i < blanks; i++) {
            calendar.add(new Span());
        }
    }

    private static void addMonthDays(
            Div calendar,
            Span monthTitle,
            YearMonth month,
            Set<LocalDate> unavailableDates,
            RentalDateRange selectedRange,
            Runnable updateSummary,
            Consumer<String> errorHandler
    ) {
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            Span dayCell = new Span(String.valueOf(day));
            boolean unavailable = unavailableDates.contains(date);
            boolean past = date.isBefore(LocalDate.now());

            styleBookingDayCell(dayCell, date, unavailable, past, selectedRange);
            if (!unavailable && !past) {
                dayCell.addClickListener(e -> {
                    selectRangeDate(date, selectedRange, unavailableDates, errorHandler);
                    updateSummary.run();
                    renderBookingCalendar(calendar, monthTitle, month, unavailableDates, selectedRange, updateSummary, errorHandler);
                });
            }

            calendar.add(dayCell);
        }
    }

    private static void styleBookingDayCell(
            Span dayCell,
            LocalDate date,
            boolean unavailable,
            boolean past,
            RentalDateRange selectedRange
    ) {
        dayCell.addClassName("dialog-calendar-day");

        if (past) {
            dayCell.addClassName("booking-day-past");
            return;
        }

        if (unavailable) {
            dayCell.addClassName("booking-day-unavailable");
            return;
        }

        if (selectedRange.isBoundary(date)) {
            dayCell.addClassName("booking-day-selectable");
            dayCell.addClassName("booking-day-boundary");
            return;
        }

        if (selectedRange.isInside(date)) {
            dayCell.addClassName("booking-day-selectable");
            dayCell.addClassName("booking-day-inside");
            return;
        }

        dayCell.addClassName("booking-day-selectable");
    }

    private static void selectRangeDate(
            LocalDate date,
            RentalDateRange selectedRange,
            Set<LocalDate> unavailableDates,
            Consumer<String> errorHandler
    ) {
        if (selectedRange.getStartDate() == null || selectedRange.isComplete()) {
            selectedRange.setStartDate(date);
            selectedRange.setEndDate(null);
            return;
        }

        if (date.isBefore(selectedRange.getStartDate())) {
            selectedRange.setStartDate(date);
            selectedRange.setEndDate(null);
            return;
        }

        if (rangeContainsUnavailableDate(selectedRange.getStartDate(), date, unavailableDates)) {
            errorHandler.accept("Selected period includes booked days. Choose another end date.");
            return;
        }

        selectedRange.setEndDate(date);
    }

    private static boolean rangeContainsUnavailableDate(LocalDate startDate, LocalDate endDate, Set<LocalDate> unavailableDates) {
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (unavailableDates.contains(date)) {
                return true;
            }
        }

        return false;
    }

    private static void updateRentalSummary(
            Vehicle vehicle,
            RentalDateRange selectedRange,
            Span selectedPeriod,
            Span totalPrice,
            Button confirm
    ) {
        if (selectedRange.getStartDate() == null) {
            selectedPeriod.setText("Click a start date, then an end date.");
            totalPrice.setText("Total price: $0.0");
            confirm.setEnabled(false);
            return;
        }

        if (selectedRange.getEndDate() == null) {
            selectedPeriod.setText("Start date: " + selectedRange.getStartDate().format(DATE_FORMATTER));
            totalPrice.setText("Total price: $0.0");
            confirm.setEnabled(false);
            return;
        }

        long days = ChronoUnit.DAYS.between(selectedRange.getStartDate(), selectedRange.getEndDate()) + 1;
        double price = vehicle.getPricePerDay() * days;

        selectedPeriod.setText("Start date: " + selectedRange.getStartDate().format(DATE_FORMATTER)
                + " | End date: " + selectedRange.getEndDate().format(DATE_FORMATTER)
                + " | Days: " + days);
        totalPrice.setText("Total price: $" + price);
        confirm.setEnabled(true);
    }

    public interface RentalConfirmationHandler {
        void confirm(Vehicle vehicle, LocalDate startDate, LocalDate endDate, Customer selectedCustomer);
    }

    private static class RentalDateRange {
        private LocalDate startDate;
        private LocalDate endDate;

        private LocalDate getStartDate() {
            return startDate;
        }

        private void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        private LocalDate getEndDate() {
            return endDate;
        }

        private void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        private boolean isComplete() {
            return startDate != null && endDate != null;
        }

        private boolean isBoundary(LocalDate date) {
            return date.equals(startDate) || date.equals(endDate);
        }

        private boolean isInside(LocalDate date) {
            return startDate != null
                    && endDate != null
                    && date.isAfter(startDate)
                    && date.isBefore(endDate);
        }
    }
}
