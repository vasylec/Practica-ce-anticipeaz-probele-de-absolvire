package com.example.ui.presenter;

import com.example.data.entity.PageSize;
import com.example.data.entity.RentFilter;
import com.example.data.entity.Rental;
import com.example.security.SecurityService;
import com.example.service.RentalService;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class RentsPresenter {
    private final RentalService rentalService;
    private final SecurityService securityService;

    private View view;
    private RentFilter currentFilter = RentFilter.ALL;

    private int pageNumber = 0;
    private int pageSize = 10;
    private int totalPages = -1;

    public RentsPresenter(RentalService rentalService, SecurityService securityService) {
        this.rentalService = rentalService;
        this.securityService = securityService;
    }

    public void setView(View view){
        this.view = view;
        pageNumber = 0;
        pageSize = 10;
        currentFilter = RentFilter.ALL;
        refreshPage();
    }

    private void setTotalPages(int pages){
        totalPages = Math.max(pages, 1);
    }

    private void resolvePageParagraph(){
        view.setPageNumberParagraph(" of " + totalPages);
    }

    private void refreshPage(){
        view.setPageSelectorValue(pageNumber + 1);
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
        view.setNavigationComponentsVisible(size != PageSize.PAGE_SIZE_ALL);
        pageNumber = 0;
        pageSize = size.getPageSize();
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

    private void updateList() {
        if (pageSize == PageSize.PAGE_SIZE_ALL.getPageSize()) {
            view.setRentals(rentalService.getRents(currentFilter));
            setTotalPages(1);
        }
        else {
            var page = rentalService.getRentsPage(currentFilter, pageNumber, pageSize);
            page = moveInsideBounds(page);
            view.setRentals(page.getContent());
            setTotalPages(page.getTotalPages());
        }

        view.setPageSelectorValue(pageNumber + 1);
        resolvePageParagraph();
    }

    private Page<Rental> moveInsideBounds(Page<Rental> page) {
        if (page.getTotalPages() > 0 && pageNumber >= page.getTotalPages()) {
            pageNumber = page.getTotalPages() - 1;
            return rentalService.getRentsPage(currentFilter, pageNumber, pageSize);
        }

        return page;
    }

    public void handleRadioGroup(RentFilter value){
        currentFilter = value;
        pageNumber = 0;
        refreshPage();
    }

    public void onRentalDoubleClicked(Rental rental) {
        if (rental != null && securityService.hasRole("ADMIN")) {
            view.openRentalDetails(rental);
        }
    }

    public interface View {
        void setRentals(List<Rental> rentals);

        void openRentalDetails(Rental rental);

        void setPageNumberParagraph(String value);

        void setNavigationComponentsVisible(boolean visible);

        void setPageSelectorValue(int pageNumber);

        void showError(String message);
    }
}
