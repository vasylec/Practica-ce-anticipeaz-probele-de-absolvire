package com.example.ui.presenter;

import com.example.data.entity.PageSize;
import com.example.data.entity.Rental;
import com.example.service.RentalService;
import com.example.ui.CustomNotification;
import com.example.ui.view.RentalList;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@UIScope
public class RentalListPresenter {
    private final RentalService rentalService;

    private RentalList view;
    private String filterText = "";
    private Rental selectedRental;

    private int pageNumber = 0;
    private int pageSize = 10;
    private int totalPages = -1;

    public RentalListPresenter(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    public void setView(RentalList view) {
        this.view = view;

        pageNumber = 0;
        pageSize = 10;

        loadReferenceData();
        refreshPage();
    }

    private void resolvePageParagraph(){
        view.setPageNumberParagraph(" of " + totalPages);
    }

    private void refreshPage(){
        updateList();
        view.setPageSelectorValue(pageNumber + 1);
        resolvePageParagraph();
    }

    public void nextPage() {
        if(pageNumber != totalPages - 1){
            pageNumber++;
            refreshPage();
        }
    }

    public void previousPage() {
        if(pageNumber > 0){
            pageNumber--;
            refreshPage();
        }
    }

    public void firstPage(){
        pageNumber = 0;
        refreshPage();
    }

    public void lastPage(){
        pageNumber = totalPages - 1;
        refreshPage();
    }

    public void onPageSizeChange(PageSize size){
        view.setNavigationComponentsVisible(size != PageSize.PAGE_SIZE_ALL);

        pageNumber = 0;

        pageSize = size.getPageSize();
        refreshPage();
    }

    public void onPageSelectorChange(int number){
        try {
            checkBoundsForPageNumber(number);
        } catch (Exception e) {
            CustomNotification.showErrorMessage("Please select a value between 1 and " + totalPages);
            refreshPage();
            return;
        }

        pageNumber = number - 1;
        refreshPage();
    }

    private void checkBoundsForPageNumber(int number) throws Exception {
        if(number < 1 || number > totalPages){
            throw new Exception("Page number out of bounds !");
        }
    }

    public void onRentalSelected(Rental rental) {
        selectedRental = rental;
        view.showRental(rental);
    }

    public void onAddRental(Rental rental) {
        if (selectedRental != null) {
            view.showError("Please deselect the rental from the list before adding a new one !");
            return;
        }

        LocalDateTime availability = rentalService.checkAvailability(rental);
        if (availability == null) {
            rentalService.addRental(rental);
            view.showSuccess("Rental added successfully !");
            selectedRental = null;
            view.clearSelection();
            refreshPage();
        }
        else {
            selectedRental = null;
            view.clearSelection();
            view.showError("The vehicle is not available at the specified date ! Please select after: " + availability);
        }
    }

    public void onSaveRental(Rental rental) {
        rentalService.updateRental(rental);
        selectedRental = null;
        view.clearSelection();
        refreshPage();
    }

    public void onDeleteRental(Rental rental) {
        rentalService.deleteRental(rental);
        selectedRental = null;
        view.clearSelection();
        refreshPage();
    }

    public void onClearForm() {
        selectedRental = null;
        view.clearSelection();
    }

    private void updateList() {
        if(pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()){
            view.setRentals(rentalService.searchRentals(filterText));
            totalPages = 1;
        }
        else{
            var page = rentalService.getRentalsPage(pageNumber, pageSize, filterText);
            page = moveInsideBounds(page);
            view.setRentals(page.getContent());
            totalPages = Math.max(page.getTotalPages(), 1);
        }
    }

    private Page<Rental> moveInsideBounds(Page<Rental> page) {
        if (page.getTotalPages() > 0 && pageNumber >= page.getTotalPages()) {
            pageNumber = page.getTotalPages() - 1;
            return rentalService.getRentalsPage(pageNumber, pageSize, filterText);
        }

        return page;
    }

    private void loadReferenceData() {
        view.setVehicles(rentalService.findAllVehicles());
        view.setCustomers(rentalService.findAllCustomers());
    }

    public void onFilterChanged(String value) {
        filterText = value == null ? "" : value;
        firstPage();
    }
}
