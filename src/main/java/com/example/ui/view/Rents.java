package com.example.ui.view;

import com.example.data.entity.PageSize;
import com.example.data.entity.RentFilter;
import com.example.data.entity.Rental;
import com.example.ui.CustomNotification;
import com.example.ui.dialog.ConfirmDialogs;
import com.example.ui.dialog.RentalDetailsDialog;
import com.example.ui.layout.MainLayout;
import com.example.ui.presenter.RentsPresenter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route(value = "service/rents", layout = MainLayout.class)
@PageTitle("Future Rents")
@RolesAllowed({"ADMIN","USER"})
public class Rents extends VerticalLayout implements RentsPresenter.View {
    private final Grid<Rental> grid = new Grid<>(Rental.class);
    private final RadioButtonGroup<RentFilter> radioGroup = new RadioButtonGroup<>();

    private final RentsPresenter presenter;

    private PageSize lastPageSize = PageSize.PAGE_SIZE_10;

    private Component navigationComponents;

    private Paragraph pageNumberParagraph;
    private NumberField pageSelector;
    private boolean updatingPageSelector;

    public Rents(RentsPresenter presenter) {
        this.presenter = presenter;

        configureGrid();
        configureRadioGroup();

        setSizeFull();
        add(
                radioGroup,
                grid,
                createBottomBar()
        );

        presenter.setView(this);
    }

    private Component createBottomBar() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignSelf(Alignment.CENTER);
        layout.setVerticalComponentAlignment(Alignment.CENTER);
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Paragraph pageSizeParagraph = new Paragraph("Page Size");

        ComboBox<PageSize> pageSize = new ComboBox<>();
        pageSize.setWidth("100px");
        pageSize.setItems(PageSize.values());
        pageSize.setValue(PageSize.PAGE_SIZE_10);
        pageSize.setItemLabelGenerator(PageSize::getLabel);
        pageSize.addValueChangeListener(e -> {
            if (e.getValue() == PageSize.PAGE_SIZE_ALL) {
                ConfirmDialogs.showAllRecords(
                        event -> {
                            presenter.onPageSizeChange(e.getValue());
                            lastPageSize = e.getValue();
                        },
                        event -> pageSize.setValue(lastPageSize)
                );
                return;
            }

            lastPageSize = e.getValue();
            presenter.onPageSizeChange(e.getValue());
        });

        HorizontalLayout pageSizeLayout = new HorizontalLayout(pageSizeParagraph, pageSize);
        pageSizeLayout.setAlignItems(Alignment.CENTER);

        navigationComponents = createNavigationButtons();

        layout.add(pageSizeLayout, navigationComponents);
        return layout;
    }

    private Component createNavigationButtons() {
        pageNumberParagraph = new Paragraph(" of 2");

        pageSelector = new NumberField();
        pageSelector.setWidth("60px");
        pageSelector.setValue(1d);
        pageSelector.setStep(1);
        pageSelector.addValueChangeListener(e -> {
            if (updatingPageSelector || e.getValue() == null) {
                return;
            }

            presenter.onPageSelectorChange(e.getValue().intValue());
        });

        Button firstPage = new Button("<<", e -> firstPage());
        Button previousPage = new Button("<", e -> previousPage());
        Button nextPage = new Button(">", e -> nextPage());
        Button lastPage = new Button(">>", e -> lastPage());

        HorizontalLayout layout = new HorizontalLayout(
                firstPage,
                previousPage,
                new Paragraph("Page "),
                pageSelector,
                pageNumberParagraph,
                nextPage,
                lastPage
        );
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    public void setPageNumberParagraph(String value){
        pageNumberParagraph.setText(value);
    }

    @Override
    public void setPageSelectorValue(int pageNumber){
        updatingPageSelector = true;
        try {
            pageSelector.setValue((double) pageNumber);
        }
        finally {
            updatingPageSelector = false;
        }
    }

    @Override
    public void setNavigationComponentsVisible(boolean visible){
        navigationComponents.setVisible(visible);
    }

    @Override
    public void showError(String message) {
        CustomNotification.showErrorMessage(message);
    }

    private void firstPage(){
        presenter.firstPage();
    }

    private void previousPage(){
        presenter.previousPage();
    }

    private void nextPage(){
        presenter.nextPage();
    }

    private void lastPage(){
        presenter.lastPage();
    }

    private void configureRadioGroup() {

        radioGroup.setItems(RentFilter.ACTIVE, RentFilter.FUTURE, RentFilter.ALL);
        radioGroup.setItemLabelGenerator(RentFilter::getLabel);
        radioGroup.setValue(RentFilter.ALL);

        radioGroup.addValueChangeListener(e -> {
            presenter.handleRadioGroup(e.getValue());
        });
    }

    private void configureGrid() {
        grid.setEmptyStateText("No records found");

        grid.removeAllColumns();

        grid.addColumn(rental -> rental.getVehicle() != null ? rental.getVehicle().getInfo() : "N/A").setSortable(true).setHeader("Vehicle");
        grid.addColumn(rental -> rental.getCustomer() != null ? rental.getCustomer().getInfo() : "N/A").setSortable(true).setHeader("Customer");
        grid.addColumn("rentalStartDate");
        grid.addColumn("rentalEndDate");
        grid.addColumn("totalPrice");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemDoubleClickListener(e -> presenter.onRentalDoubleClicked(e.getItem()));
    }

    @Override
    public void setRentals(List<Rental> list){
        grid.setItems(list);
    }

    @Override
    public void openRentalDetails(Rental rental) {
        RentalDetailsDialog.open(rental);
    }
}
