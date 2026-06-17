package com.example.service;

import com.example.data.entity.AppUser;
import com.example.data.entity.Payment;
import com.stripe.model.checkout.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentProcessor {

    private static final String PAID_STATUS = "paid";

    private final AppUserService appUserService;
    private final PaymentService paymentService;
    private final PaymentUpdateBroadcaster paymentUpdateBroadcaster;

    public PaymentProcessor(AppUserService appUserService,
                            PaymentService paymentService,
                            PaymentUpdateBroadcaster paymentUpdateBroadcaster) {
        this.appUserService = appUserService;
        this.paymentService = paymentService;
        this.paymentUpdateBroadcaster = paymentUpdateBroadcaster;
    }

    @Transactional
    public PaymentProcessingResult process(Session session) {
        Payment existingPayment = paymentService.getPayment(session.getId());
        if (existingPayment != null) {
            if (belongsToCurrentUser(existingPayment)) {
                return PaymentProcessingResult.alreadyProcessed();
            }

            return PaymentProcessingResult.accessDenied();
        }

        Payment payment = createPayment(session);
        paymentService.save(payment);

        if (payment.isProcessed()) {
            BigDecimal updatedBalance = appUserService.deposit(payment.getAmount().doubleValue());
            paymentUpdateBroadcaster.paymentReceived(
                    payment.getUser().getId(),
                    payment.getAmount(),
                    updatedBalance
            );
            return PaymentProcessingResult.success();
        }

        return PaymentProcessingResult.failed();
    }

    private Payment createPayment(Session session) {
        String amountStr = session.getMetadata().get("amount");
        double amount = Double.parseDouble(amountStr) / 100;

        Payment payment = new Payment(
                appUserService.getLoggedInUser(),
                BigDecimal.valueOf(amount),
                session.getId(),
                session.getPaymentIntent()
        );
        payment.setProcessed(PAID_STATUS.equals(session.getPaymentStatus()));

        return payment;
    }

    private boolean belongsToCurrentUser(Payment payment) {
        AppUser loggedInUser = appUserService.getLoggedInUser();
        return loggedInUser != null
                && payment.getUser() != null
                && loggedInUser.getId().equals(payment.getUser().getId());
    }

    public enum Status {
        SUCCESS,
        FAILED,
        ALREADY_PROCESSED,
        ACCESS_DENIED,
        ERROR
    }

    public record PaymentProcessingResult(Status status) {
        public static PaymentProcessingResult success() {
            return new PaymentProcessingResult(Status.SUCCESS);
        }

        public static PaymentProcessingResult failed() {
            return new PaymentProcessingResult(Status.FAILED);
        }

        public static PaymentProcessingResult alreadyProcessed() {
            return new PaymentProcessingResult(Status.ALREADY_PROCESSED);
        }

        public static PaymentProcessingResult accessDenied() {
            return new PaymentProcessingResult(Status.ACCESS_DENIED);
        }

        public static PaymentProcessingResult error() {
            return new PaymentProcessingResult(Status.ERROR);
        }
    }
}
