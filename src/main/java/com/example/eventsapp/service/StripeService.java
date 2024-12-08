package com.example.eventsapp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createCheckoutSession(String eventName, Long eventId, Long participantId, double price) throws StripeException {
        // Initialiser Stripe avec la clé secrète
        Stripe.apiKey = stripeApiKey;

        // Créer une session de paiement Stripe
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:9090/payments/success?eventId=" + eventId + "&participantId=" + participantId)
                .setCancelUrl("http://localhost:9090/payments/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd") // Devise
                                                .setUnitAmount((long) (price * 100)) // Montant en cents
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(eventName)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);
        return session.getUrl();

    }
}
