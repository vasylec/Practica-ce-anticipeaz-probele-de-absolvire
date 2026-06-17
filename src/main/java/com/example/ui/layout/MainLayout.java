package com.example.ui.layout;

import com.example.ui.CustomNotification;
import com.example.ui.dialog.DepositDialog;
import com.example.ui.presenter.MainLayoutPresenter;
import com.example.ui.view.*;
import com.example.ui.view.charts.CarRentalChart;
import com.example.ui.view.charts.RentalsPerMonth;
import com.example.ui.view.charts.RevenueChart;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.shared.Registration;
import jakarta.annotation.security.PermitAll;

import static com.example.ui.dialog.ConfirmDialogs.logoutDialog;

@Layout
@PermitAll
public class MainLayout extends AppLayout implements AfterNavigationObserver, MainLayoutPresenter.View {
    private static final String BALANCE_FONT_SIZE = "20px";
    private static final String SECTION_MARGIN = "40px";

    private final MainLayoutPresenter presenter;
    private final Span balanceSpan = new Span();

    private Registration refreshBalanceRegistration;

    public MainLayout(MainLayoutPresenter presenter) {
        this.presenter = presenter;
        this.presenter.setView(this);

        setDrawerOpened(false);
        createHeader();
        createDrawer();
        addAttachListener(event -> presenter.registerPaymentUpdates(event.getUI()));
        addDetachListener(event -> presenter.unregisterPaymentUpdates());
    }

    private void createHeader() {
        updateBalance();
        styleBalance();

        HorizontalLayout header = presenter.isAdmin()
                ? new HorizontalLayout(new DrawerToggle())
                : new HorizontalLayout(new DrawerToggle(), createBalanceActions());

        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();

        addToNavbar(header);
    }

    private void createDrawer() {
        if (presenter.isAdmin()) {
            createDrawerForAdmin();
            return;
        }

        createDrawerForUser();
    }

    private void createDrawerForUser() {
        VerticalLayout links = new VerticalLayout();
        links.add(
                createNavigationButton("Book a Vehicle", BookVehicleList.class),
                createNavigationButton("My Rents", Rents.class)
        );

        setButtonPressDesign(links);
        addToDrawer(createDrawerMenu(links));
    }

    private void createDrawerForAdmin() {
        VerticalLayout links = new VerticalLayout();
        links.add(
                createSectionTitle("Database"),
                createNavigationButton("Vehicle List", VehicleList.class),
                createNavigationButton("Customer List", CustomerList.class),
                createNavigationButton("Rental List", RentalList.class),

                createSectionTitle("Services"),
                createNavigationButton("Book a Vehicle", BookVehicleList.class),
                createNavigationButton("Rented Vehicles", Rents.class),
                createNavigationButton("Rents per Vehicle", VehiclesTotalRentsList.class),

                createSectionTitle("Charts"),
                createNavigationButton("Vehicle Rentals", CarRentalChart.class),
                createNavigationButton("Rentals per Month", RentalsPerMonth.class),
                createNavigationButton("Revenue per month", RevenueChart.class)
        );

        setButtonPressDesign(links);
        addToDrawer(createDrawerMenu(links));
    }

    private Button createNavigationButton(String text, Class<? extends Component> navigationTarget) {
        return new Button(text, event -> getUI().ifPresent(ui -> ui.navigate(navigationTarget)));
    }

    private H1 createSectionTitle(String text) {
        H1 title = new H1(text);
        title.getStyle().set("margin-top", SECTION_MARGIN);
        title.getStyle().set("margin-bottom", SECTION_MARGIN);
        return title;
    }

    private VerticalLayout createDrawerMenu(VerticalLayout links) {
        Button logout = createLogoutButton();

        VerticalLayout menu = new VerticalLayout();
        menu.setSizeFull();
        menu.setPadding(true);
        menu.add(links);
        menu.expand(links);
        menu.setAlignSelf(FlexComponent.Alignment.CENTER, logout);
        menu.add(createLogoutLayout(logout));

        return menu;
    }

    private VerticalLayout createLogoutLayout(Button logout) {
        VerticalLayout logoutLayout = new VerticalLayout(logout);
        logoutLayout.setPadding(true);
        return logoutLayout;
    }

    private Button createLogoutButton() {
        Button logout = new Button("Logout", event -> logoutDialog(e -> presenter.onLogoutConfirmed()));
        logout.setThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        return logout;
    }

    private void setButtonPressDesign(VerticalLayout links) {
        links.getChildren().forEach(item -> {
            if (item instanceof Button button) {
                button.addClickListener(event -> selectButton(links, button));
            }
        });
    }

    private void selectButton(VerticalLayout links, Button selectedButton) {
        links.getChildren().forEach(child -> {
            if (child instanceof Button button) {
                button.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        });

        selectedButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    }

    private void styleBalance() {
        balanceSpan.getStyle().setFontSize(BALANCE_FONT_SIZE);
        balanceSpan.getStyle().setFontWeight("normal");
    }

    private HorizontalLayout createBalanceActions() {
        HorizontalLayout balanceLayout = new HorizontalLayout(balanceSpan, createDepositButton());
        balanceLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        return balanceLayout;
    }

    private Button createDepositButton() {
        Button deposit = new Button("Deposit", event -> DepositDialog.open(presenter::onDeposit));
        deposit.setThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        return deposit;
    }

    public void updateBalance() {
        presenter.refreshBalance();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        if (refreshBalanceRegistration != null) {
            refreshBalanceRegistration.remove();
            refreshBalanceRegistration = null;
        }

        Component content = getContent();

        if (content instanceof BookVehicleList bookVehicleList) {
            refreshBalanceRegistration = bookVehicleList.addListener(
                    BookVehicleList.RefreshBalance.class,
                    e -> updateBalance()
            );
        }

        updateBalance();
    }

    @Override
    public void setBalance(String balance) {
        balanceSpan.setText(balance);
    }

    @Override
    public void showSuccess(String message) {
        CustomNotification.showSuccessMessageLongDuration(message);
    }

    @Override
    public void showError(String message) {
        CustomNotification.showErrorMessage(message);
    }
}
