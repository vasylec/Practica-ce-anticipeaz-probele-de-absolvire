package com.example.ui.presenter;

import com.example.security.SecurityService;
import com.example.service.AppUserService;
import com.example.service.PaymentUpdateBroadcaster;
import com.example.service.PaymentUpdateBroadcaster.PaymentUpdate;
import com.example.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@UIScope
public class MainLayoutPresenter {
    private final SecurityService securityService;
    private final AppUserService appUserService;
    private final StripeService stripeService;
    private final PaymentUpdateBroadcaster paymentUpdateBroadcaster;

    private View view;
    private Registration paymentUpdateRegistration;

    public MainLayoutPresenter(SecurityService securityService,
                               AppUserService appUserService,
                               StripeService stripeService,
                               PaymentUpdateBroadcaster paymentUpdateBroadcaster) {
        this.securityService = securityService;
        this.appUserService = appUserService;
        this.stripeService = stripeService;
        this.paymentUpdateBroadcaster = paymentUpdateBroadcaster;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isAdmin() {
        return securityService.hasRole("ADMIN");
    }

    public void refreshBalance() {
        if (isAdmin()) {
            view.setBalance("");
            return;
        }

        view.setBalance(formatBalance(BigDecimal.valueOf(appUserService.getBalance())));
    }

    public void registerPaymentUpdates(UI ui) {
        if (isAdmin() || paymentUpdateRegistration != null) {
            return;
        }

        Long userId = appUserService.getLoggedInUser().getId();
        paymentUpdateRegistration = paymentUpdateBroadcaster.register(userId, ui, this::onPaymentReceived);
    }

    public void unregisterPaymentUpdates() {
        if (paymentUpdateRegistration != null) {
            paymentUpdateRegistration.remove();
            paymentUpdateRegistration = null;
        }
    }

    private void onPaymentReceived(PaymentUpdate paymentUpdate) {
        view.setBalance(formatBalance(paymentUpdate.updatedBalance()));
        view.showSuccess(
                "Payment received: $"
                        + formatAmount(paymentUpdate.amount())
                        + ". Updated balance: $"
                        + formatAmount(paymentUpdate.updatedBalance())
                        + "."
        );
    }

    public boolean onDeposit(Double amount) {
        if (amount == null || amount <= 0) {
            view.showError("Enter a positive amount.");
            return false;
        }

        if(amount < 50 || amount > 10000){
            view.showError("Enter a value between 50$ and 10 000$.");
            return false;
        }

        Session session = null;
        try {
            amount *= 100;
            Long amountToStripe = amount.longValue();
            session = stripeService.createPayment(amountToStripe);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

        String url = session.getUrl();
        UI.getCurrent().getPage().open(url);

        refreshBalance();
        return true;
    }

    public void onLogoutConfirmed() {
        unregisterPaymentUpdates();
        securityService.logout();
    }

    private String formatBalance(BigDecimal balance) {
        return "Balance: $" + formatAmount(balance);
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public interface View {
        void setBalance(String balance);

        void showSuccess(String message);

        void showError(String message);
    }
}
