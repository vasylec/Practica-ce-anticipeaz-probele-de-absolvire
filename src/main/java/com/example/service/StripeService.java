package com.example.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    public String domainString;

    public StripeService(
            @Value("${stripe.secret-key}") String key,
            @Value("${site.domain}") String domain) {
        Stripe.apiKey = key;
        this.domainString = domain;
    }

    public Session createPayment(Long amount) throws StripeException {
        SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(this.domainString + "/payment?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(this.domainString + "/payment")
                .putMetadata("amount", amount.toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Deposit Rental Car")
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(sessionCreateParams);
        return session;
    }
}
