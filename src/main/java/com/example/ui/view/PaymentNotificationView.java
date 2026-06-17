package com.example.ui.view;

import com.example.service.PaymentProcessor.PaymentProcessingResult;
import com.example.ui.presenter.PaymentNotificationPresenter;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "payment", autoLayout = false)
@PermitAll
public class PaymentNotificationView extends VerticalLayout implements BeforeEnterObserver {

    private final PaymentNotificationPresenter presenter;
    private BeforeEnterEvent beforeEnterEvent;

    public PaymentNotificationView(PaymentNotificationPresenter presenter) {
        this.presenter = presenter;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private Session getSession(BeforeEnterEvent event) {
        String sessionId = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("session_id", List.of(""))
                .get(0);

        if (sessionId == null || sessionId.isEmpty()) {
            redirectToHelloView();
            return null;
        }

        try {
            return Session.retrieve(sessionId);
        } catch (StripeException e) {
            redirectToHelloView();
            return null;
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        beforeEnterEvent = event;
        Session session = getSession(event);
        if (session != null) {
            handleResult(presenter.processPayment(session));
        }
    }

    public void setNotification(String notification) {
        add(new H1(notification));
    }

    public void redirectToHelloView() {
        if (beforeEnterEvent != null) {
            beforeEnterEvent.forwardTo(HelloView.class);
        }
    }

    private void handleResult(PaymentProcessingResult result) {
        switch (result.status()) {
            case SUCCESS -> setNotification("Payment successful, you may now close this page");
            case FAILED, ALREADY_PROCESSED -> redirectToHelloView();
            case ACCESS_DENIED -> setNotification("Access Denied");
            case ERROR -> setNotification("Error validating payment");
        }
    }
}
