package com.example.ui.dialog;

import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public final class AvailabilityDialog {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private AvailabilityDialog() {
    }

    public static void open(Vehicle vehicle, List<Rental> rentals) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Availability for " + vehicle.getInfo());
        dialog.setWidth("420px");

        AtomicReference<YearMonth> displayedMonth = new AtomicReference<>(YearMonth.now());
        Span monthTitle = new Span();
        Div calendar = new Div();
        configureCalendarGrid(calendar);

        Button previousYear = new Button("<<");
        Button previous = new Button(VaadinIcon.ANGLE_LEFT.create());
        Button next = new Button(VaadinIcon.ANGLE_RIGHT.create());
        Button nextYear = new Button(">>");
        Button close = new Button("Close", event -> dialog.close());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);

        previousYear.addClickListener(event -> {
            displayedMonth.set(displayedMonth.get().minusYears(1));
            renderAvailabilityCalendar(calendar, monthTitle, displayedMonth.get(), rentals);
        });
        nextYear.addClickListener(event -> {
            displayedMonth.set(displayedMonth.get().plusYears(1));
            renderAvailabilityCalendar(calendar, monthTitle, displayedMonth.get(), rentals);
        });
        previous.addClickListener(event -> {
            displayedMonth.set(displayedMonth.get().minusMonths(1));
            renderAvailabilityCalendar(calendar, monthTitle, displayedMonth.get(), rentals);
        });
        next.addClickListener(event -> {
            displayedMonth.set(displayedMonth.get().plusMonths(1));
            renderAvailabilityCalendar(calendar, monthTitle, displayedMonth.get(), rentals);
        });

        next.addClickShortcut(Key.ARROW_RIGHT);
        previous.addClickShortcut(Key.ARROW_LEFT);

        HorizontalLayout navigation = new HorizontalLayout(previousYear, previous, monthTitle, next, nextYear);
        navigation.setWidthFull();
        navigation.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navigation.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        Span legend = new Span("Red days are unavailable.");
        legend.addClassName("dialog-calendar-legend");

        VerticalLayout layout = new VerticalLayout(navigation, calendar, legend, close);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        renderAvailabilityCalendar(calendar, monthTitle, displayedMonth.get(), rentals);

        dialog.add(layout);
        dialog.open();
    }

    private static void configureCalendarGrid(Div calendar) {
        calendar.addClassName("dialog-calendar-grid");
    }

    private static void renderAvailabilityCalendar(
            Div calendar,
            Span monthTitle,
            YearMonth month,
            List<Rental> rentals
    ) {
        calendar.removeAll();
        monthTitle.setText(month.format(MONTH_FORMATTER));

        addWeekdayHeaders(calendar);
        addBlankDays(calendar, month);
        addMonthDays(calendar, month, rentals);
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

    private static void addMonthDays(Div calendar, YearMonth month, List<Rental> rentals) {
        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            Span dayCell = new Span(String.valueOf(day));
            styleDayCell(dayCell, isUnavailable(date, rentals));
            calendar.add(dayCell);
        }
    }

    private static void styleDayCell(Span dayCell, boolean unavailable) {
        dayCell.addClassName("dialog-calendar-day");

        if (unavailable) {
            dayCell.addClassName("availability-day-unavailable");
        }
    }

    private static boolean isUnavailable(LocalDate date, List<Rental> rentals) {
        return rentals.stream().anyMatch(rental -> isWithinRental(date, rental));
    }

    private static boolean isWithinRental(LocalDate date, Rental rental) {
        if (rental.getRentalStartDate() == null || rental.getRentalEndDate() == null) {
            return false;
        }

        LocalDate start = rental.getRentalStartDate().toLocalDate();
        LocalDate end = getDisplayEndDate(rental.getRentalEndDate());

        return !date.isBefore(start) && !date.isAfter(end);
    }

    private static LocalDate getDisplayEndDate(LocalDateTime rentalEndDate) {
        LocalDate end = rentalEndDate.toLocalDate();

        if (LocalTime.MIDNIGHT.equals(rentalEndDate.toLocalTime())) {
            return end.minusDays(1);
        }

        return end;
    }
}
