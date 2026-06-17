package com.example.ui.presenter;

import com.example.data.entity.Customer;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.security.SecurityService;
import com.example.service.AppUserService;
import com.example.service.RentalService;
import com.example.ui.view.BookVehicleList;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@UIScope
public class BookVehicleListPresenter {
    private final RentalService rentalService;
    private final AppUserService userService;
    private final SecurityService securityService;

    private BookVehicleList view;

    public BookVehicleListPresenter(RentalService rentalService,
                                    AppUserService userService,
                                    SecurityService securityService) {
        this.rentalService = rentalService;
        this.userService = userService;
        this.securityService = securityService;
    }

    public void setView(BookVehicleList view) {
        this.view = view;
        refreshVehicles();
    }

    public void onMakeRentalClicked(Vehicle vehicle) {
        if (vehicle == null) {
            view.showError("Select a vehicle from the list !");
            return;
        }

        List<Customer> customers = isAdmin() ? rentalService.findAllCustomers() : List.of();
        List<Rental> rentals = rentalService.findRentalsByVehicle(vehicle);
        view.openRentalDialog(vehicle, isAdmin(), customers, rentals);
    }

    public void onCheckAvailabilityClicked(Vehicle vehicle) {
        if (vehicle == null) {
            view.showError("Select a vehicle from the list !");
            return;
        }

        view.openAvailabilityDialog(vehicle, rentalService.findRentalsByVehicle(vehicle));
    }

    public void handleShowOnlyFreeVehicles(boolean value){
        if(value){
            setAvailableVehicles();
        }
        else{
            refreshVehicles();
        }
    }

    public void onConfirmRental(Vehicle vehicle, LocalDate startDate, LocalDate endDate, Customer selectedCustomer) {
        boolean forAdmin = isAdmin();

        if (startDate == null || endDate == null) {
            view.showError("Select start and end date");
            return;
        }

        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double price = days * vehicle.getPricePerDay();

        if (days < 1) {
            view.showError("End date must be after start date");
            return;
        }

        if (!forAdmin && days > 30) {
            view.showError("Select between 1 and 30 days");
            return;
        }

        if (!forAdmin && userService.getBalance() < price) {
            view.showError("Insufficient funds");
            return;
        }

        if (forAdmin && selectedCustomer == null) {
            view.showError("Select customer first");
            return;
        }

        LocalDateTime rentalStartDate = startDate.atStartOfDay();
        LocalDateTime rentalEndDate = endDate.plusDays(1).atStartOfDay();
        Rental rental = new Rental(
                forAdmin ? selectedCustomer : userService.getCustomer(),
                vehicle,
                rentalStartDate,
                rentalEndDate,
                BigDecimal.valueOf(price)
        );

        LocalDateTime availability = rentalService.checkAvailability(rental);
        if (availability != null) {
            view.showError("The vehicle is not available at the specified date ! Please select after: " + availability);
            return;
        }

        rentalService.addRental(rental);
        refreshVehicles();
        view.showSuccess("Rental successfully placed");
        view.closeDialog();

        if (!forAdmin) {
            userService.subtractBalance(price);
            view.refreshBalance();
        }
    }

    private void refreshVehicles() {
        view.setVehicles(rentalService.findAllVehicles());
    }

    private void setAvailableVehicles(){
        view.setVehicles(rentalService.getAvailableVehicles());
    }

    private boolean isAdmin() {
        return securityService.hasRole("ADMIN");
    }
}
