package com.example.ui.presenter;

import com.example.data.entity.Customer;
import com.example.data.entity.PageSize;
import com.example.service.AppUserService;
import com.example.service.CustomerService;
import com.example.ui.form.CustomerForm;
import com.example.ui.view.CustomerList;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class CustomerListPresenter {

    private final CustomerService customerService;
    private final AppUserService appUserService;
    private CustomerList view;
    private String filterText = "";
    private Customer selectedCustomer;

    private int pageNumber = 0;
    private int pageSize = 10;
    private int totalPages = -1;


    public CustomerListPresenter(CustomerService customerService, AppUserService appUserService) {
        this.customerService = customerService;
        this.appUserService = appUserService;

        getTotalPages();
    }

    public void setView(CustomerList view) {
        this.view = view;

        pageNumber = 0;
        pageSize = 10;

        getTotalPages();
        refreshPage();
    }

    private void getTotalPages(){
        if (pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()) {
            totalPages = 1;
            return;
        }

        totalPages = Math.max(customerService.getTotalPagesNumber(pageSize, filterText), 1);
    }

    private void resolvePageParagraph(){
        view.setPageNumberParagraph(" of " + totalPages);
    }

    private void refreshPage(){
        view.setPageSelectorValue(pageNumber + 1);
        resolvePageParagraph();
        updateList();
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
        if(size == PageSize.PAGE_SIZE_ALL){
            view.setNavigationComponentsVisible(false);
        }
        else{
            view.setNavigationComponentsVisible(true);
        }

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

    public void onCustomerSelected(Customer selected) {
        selectedCustomer = selected;
        view.showCustomer(selected);
    }

    public void onAddCustomer(CustomerForm.FormData formData) {
        if (selectedCustomer != null) {
            view.showFormError("Please deselect the customer from the list before adding a new one !");
            return;
        }

        if (!isComplete(formData)) {
            view.showFormError("Please fill all the fields !");
            return;
        }

        Customer customer = formData.toCustomer();

        if (!isRentalHistoryValid(customer)) {
            view.showError("Customer may not have more Late Returns than rents !");
            return;
        }

        customerService.addCustomer(customer);
        view.showSuccess("Customer successfully added !");
        updateList();
    }

    public void onUpdateCustomer(Customer customer) {
        if (!isRentalHistoryValid(customer)) {
            view.showError("Customer may not have more Late Returns than rents !");
            return;
        }

        customerService.updateCustomer(customer);
        view.showSuccess("Customer successfully updated !");
        updateList();
    }

    public void onDeleteCustomer(Customer customer) {
        int status = customerService.deleteCustomer(customer);
        if (status == 0) {
            view.showSuccess("Customer successfully deleted !");
            selectedCustomer = null;
            view.hideForm();
        }
        else if (status == 1) {
            view.showError("Customer can not be deleted because it is used as foreign key !");
        }

        updateList();
    }

    public String getUsernameForCustomer(Customer customer) {
        return appUserService.getUsernameForCustomer(customer);
    }

    private void updateList() {
        if(pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()){
            view.setCustomers(customerService.search(filterText));
        }
        else{
            var page = customerService.getCustomersPage(pageNumber, pageSize, filterText);
            view.setCustomers(page.getContent());
        }
    }

    public void onFilterChanged(String value) {
        filterText = value == null ? "" : value;
        getTotalPages();
        firstPage();
    }

    private boolean isComplete(CustomerForm.FormData formData) {
        return !isBlank(formData.customerName())
                && !isBlank(formData.customerSecondName())
                && !isBlank(formData.phone())
                && formData.totalRentals() != null
                && formData.lateReturns() != null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isRentalHistoryValid(Customer customer) {
        return customer.getTotalRentals() >= customer.getLateReturns();
    }
}
