package com.example.ui.presenter;

import com.example.data.entity.PageSize;
import com.example.data.entity.Vehicle;
import com.example.service.RentalService;
import com.example.service.VehicleService;
import com.example.ui.view.VehicleList;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class VehicleListPresenter {
    private final VehicleService vehicleService;
    private final RentalService rentalService;

    private VehicleList view;
    private String filterText = "";

    private int pageNumber = 0;
    private int pageSize = 10;
    private int totalPages = -1;

    public VehicleListPresenter(VehicleService service, RentalService rentalService) {
        this.vehicleService = service;
        this.rentalService = rentalService;

        getTotalPages();
    }

    private void getTotalPages(){
        if (pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()) {
            totalPages = 1;
            return;
        }

        totalPages = Math.max(vehicleService.getTotalPagesNumber(pageSize, filterText), 1);
    }

    private void resolvePageParagraph(){
        view.setPageNumberParagraph(" of " + totalPages);
    }

    private void refreshPage(){
        view.setPageSelectorValue(pageNumber + 1);
        resolvePageParagraph();
        updateList();
    }

    public void setView(VehicleList view) {
        this.view = view;

        pageNumber = 0;
        pageSize = 10;

        getTotalPages();
        refreshPage();
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
        getTotalPages();
        refreshPage();
    }

    public void onPageSelectorChange(int number){
        if (number < 1 || number > totalPages) {
            view.showError("Please select a value between 1 and " + totalPages);
            refreshPage();
            return;
        }

        pageNumber = number - 1;
        refreshPage();
    }

    private void updateList(){
        if(pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()){
            view.setVehicles(vehicleService.search(filterText));
        }
        else{
            var page = vehicleService.getVehiclesPage(pageNumber, pageSize, filterText);
            view.setVehicles(page.getContent());
        }
    }

    public void onFilterChanged(String value) {
        filterText = value == null ? "" : value;
        getTotalPages();
        firstPage();
    }

    public void onAddVehicleClicked() {
        view.showAddVehicleForm();
    }

    public void onCheckAvailabilityClicked(Vehicle vehicle) {
        if (vehicle == null) {
            view.showError("Select a vehicle from the list !");
            return;
        }

        view.openAvailabilityDialog(vehicle, rentalService.findRentalsByVehicle(vehicle));
    }

    public void onVehicleSelected(Vehicle selected) {
        if (selected == null) {
            view.hideVehicleForm();
            return;
        }

        view.showSelectedVehicle(selected);
    }

    public void onSaveVehicle(Vehicle vehicle) {
        try {
            vehicleService.updateVehicle(vehicle);
            updateList();
            view.showSuccess("Vehicle successfully updated !");
        }
        catch (DataIntegrityViolationException exception) {
            view.showError(getDataIntegrityMessage(exception));
        }
        catch (Exception exception) {
            view.showError("Error, vehicle was not updated !");
        }
    }

    public void onDeleteVehicle(Vehicle vehicle) {
        int status = vehicleService.deleteVehicle(vehicle);

        if (status == 0) {
            view.showSuccess("Vehicle successfully deleted !");
            view.hideVehicleForm();
        }
        else if (status == 1) {
            view.showError("Vehicle can not be deleted because it is used as foreign key !");
        }

        updateList();
    }

    public void onAddVehicle(Vehicle vehicle) {
        try {
            vehicleService.addVehicle(vehicle);
            updateList();
            view.showSuccess("Vehicle successfully added !");
        }
        catch (DataIntegrityViolationException exception) {
            view.showError(getDataIntegrityMessage(exception));
        }
        catch (Exception exception) {
            view.showError("Error, vehicle was not added !");
        }
    }

    public void onCloseVehicleForm() {
        view.hideVehicleForm();
    }

    private String getDataIntegrityMessage(DataIntegrityViolationException exception) {
        String message = exception.getMessage();
        if (message != null && message.contains("IMAGE")) {
            return "Error, Image link is too long !";
        }

        return "Error, one or more fields have to many characters !";
    }
}
