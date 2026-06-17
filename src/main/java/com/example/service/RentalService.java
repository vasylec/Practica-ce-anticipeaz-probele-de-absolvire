package com.example.service;

import com.example.data.entity.Customer;
import com.example.data.entity.RentFilter;
import com.example.data.entity.Rental;
import com.example.data.entity.Vehicle;
import com.example.data.repository.CustomerRepository;
import com.example.data.repository.RentalRepository;
import com.example.data.repository.VehicleRepository;
import com.example.security.SecurityService;
import com.example.specification.RentalSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final SecurityService securityService;
    private final AppUserService userService;

    public RentalService(RentalRepository rentalRepository,
            VehicleRepository vehicleRepository,
            CustomerRepository customerRepository,
            SecurityService securityService,
            AppUserService userService) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.securityService = securityService;
        this.userService = userService;
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll(RentalSpecification.search(null));
    }

    public List<Rental> searchRentals(String filter) {
        return rentalRepository.findAll(RentalSpecification.search(filter));
    }

    public List<Rental> findRentalsByVehicle(Vehicle vehicle) {
        return rentalRepository.findByVehicle(vehicle);
    }

    public void updateRental(Rental rental) {
        rentalRepository.save(rental);
    }

    @Transactional
    public void deleteRental(Rental rental) {
        Customer customer = rental.getCustomer();
        customer.decrementTotalRentals();
        rentalRepository.delete(rental);
        customerRepository.save(customer);
    }

    @Transactional
    public void addRental(Rental rental) {
        Customer customer = rental.getCustomer();
        customer.incrementTotalRentals();
        rentalRepository.save(rental);
        customerRepository.save(customer);
    }

    public LocalDateTime checkAvailability(Rental rental) {
        return rentalRepository
                .findFirstByVehicleAndRentalStartDateBeforeAndRentalEndDateAfterOrderByRentalEndDateDesc(
                        rental.getVehicle(),
                        rental.getRentalEndDate(),
                        rental.getRentalStartDate())
                .map(Rental::getRentalEndDate)
                .orElse(null);
    }

    public List<Object[]> getVehiclesRentalsForChart() {
        return rentalRepository.getVehicleRentalsForChart();
    }

    public List<Object[]> getRentalsPerMonthForChart() {
        return rentalRepository.getTotalRentsPerMonth();
    }

    public List<Object[]> getRevenuePerMonthForChart() {
        return rentalRepository.getRevenuePerMonth();
    }

    public List<Vehicle> getAvailableVehicles() {
        return rentalRepository.findAvailableVehicles();
    }

    public int countRentsForVehicle(Vehicle vehicle) {
        return rentalRepository.countRentalByVehicle(vehicle);
    }

    public List<Rental> getActiveRents() {
        return getRents(RentFilter.ACTIVE);
    }

    public Page<Rental> getActiveRentsPage(int page, int size) {
        return getRentsPage(RentFilter.ACTIVE, page, size);
    }

    public List<Rental> getAllRents() {
        return getRents(RentFilter.ALL);
    }

    public Page<Rental> getAllRentsPage(int page, int size) {
        return getRentsPage(RentFilter.ALL, page, size);
    }

    public List<Rental> getFutureRents() {
        return getRents(RentFilter.FUTURE);
    }

    public Page<Rental> getFutureRentsPage(int page, int size) {
        return getRentsPage(RentFilter.FUTURE, page, size);
    }

    public List<Rental> getRents(RentFilter filter) {
        return rentalRepository.findAll(getRentsSpecification(filter));
    }

    public Page<Rental> getRentsPage(RentFilter filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return rentalRepository.findAll(getRentsSpecification(filter), pageable);
    }

    private Specification<Rental> getRentsSpecification(RentFilter filter) {
        Specification<Rental> specification = statusSpecification(filter);

        if (!securityService.hasRole("ADMIN")) {
            Customer customer = userService.getCustomer();
            specification = specification.and((root, query, cb) -> cb.equal(root.get("customer"), customer));
        }

        return specification;
    }

    public Page<Rental> getRentalsPage(int page, int size, String filter) {
        Pageable pageable = PageRequest.of(page, size);

        return rentalRepository.findAll(RentalSpecification.search(filter), pageable);
    }

    private Specification<Rental> statusSpecification(RentFilter filter) {
        return (root, query, cb) -> {
            LocalDateTime now = LocalDateTime.now();

            if (filter == RentFilter.ACTIVE) {
                return cb.and(
                        cb.lessThanOrEqualTo(root.get("rentalStartDate"), now),
                        cb.greaterThanOrEqualTo(root.get("rentalEndDate"), now));
            }

            if (filter == RentFilter.FUTURE) {
                return cb.greaterThan(root.get("rentalStartDate"), now);
            }

            return cb.conjunction();
        };
    }
}
