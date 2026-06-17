package com.example.ui.presenter;

import com.example.service.PaymentProcessor;
import com.example.service.PaymentProcessor.PaymentProcessingResult;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationPresenter {

    private final PaymentProcessor paymentProcessor;

    public PaymentNotificationPresenter(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public PaymentProcessingResult processPayment(Session session) {
        try {
            return paymentProcessor.process(session);
        } catch (Exception e) {
            return PaymentProcessingResult.error();
        }
    }
}
