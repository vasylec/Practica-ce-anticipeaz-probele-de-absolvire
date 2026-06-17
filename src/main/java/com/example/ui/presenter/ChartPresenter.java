package com.example.ui.presenter;

import com.example.service.RentalService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class ChartPresenter {
    private final RentalService service;

    public ChartPresenter(RentalService service) {
        this.service = service;
    }

    public List<NamedChartPoint> getCarRentalData() {
        List<NamedChartPoint> points = new ArrayList<>();

        for (Object[] item : service.getVehiclesRentalsForChart()) {
            String vehicle = item[0] + " " + item[1];
            Number rentals = (Number) item[2];
            points.add(new NamedChartPoint(vehicle, rentals));
        }

        return points;
    }

    public List<TimelineChartPoint> getRentalsPerMonthData() {
        List<TimelineChartPoint> points = new ArrayList<>();
        LocalDate previousDate = null;

        for (Object[] item : service.getRentalsPerMonthForChart()) {
            int month = ((Number) item[0]).intValue();
            int year = ((Number) item[1]).intValue();
            Number rentals = (Number) item[2];
            LocalDate currentDate = LocalDate.of(year, month, 1).plusDays(1);

            if (previousDate != null) {
                addEmptyMonths(points, previousDate, currentDate);
            }

            points.add(new TimelineChartPoint(toMillis(currentDate), rentals));
            previousDate = currentDate;
        }

        return points;
    }

    public List<TimelineChartPoint> getRevenuePerMonthData() {
        List<TimelineChartPoint> points = new ArrayList<>();
        LocalDate previousDate = null;

        for (Object[] item : service.getRevenuePerMonthForChart()) {
            int month = ((Number) item[0]).intValue();
            int year = ((Number) item[1]).intValue();
            BigDecimal revenue = (BigDecimal) item[2];

            LocalDate currentDate = LocalDate.of(year, month, 1).plusDays(1);

            if (previousDate != null) {
                addEmptyMonths(points, previousDate, currentDate);
            }

            points.add(new TimelineChartPoint(toMillis(currentDate), revenue));
            previousDate = currentDate;
        }

        return points;
    }

    private void addEmptyMonths(List<TimelineChartPoint> points, LocalDate previousDate, LocalDate currentDate) {
        LocalDate temp = previousDate.plusMonths(1);

        while (temp.isBefore(currentDate)) {
            points.add(new TimelineChartPoint(toMillis(temp), 0));
            temp = temp.plusMonths(1);
        }
    }

    private long toMillis(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public record NamedChartPoint(String name, Number value) {
    }

    public record TimelineChartPoint(long timestamp, Number value) {
    }
}
