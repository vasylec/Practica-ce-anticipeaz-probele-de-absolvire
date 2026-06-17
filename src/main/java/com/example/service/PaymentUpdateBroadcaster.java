package com.example.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.shared.Registration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Component
public class PaymentUpdateBroadcaster {

    private final Map<Long, List<PaymentUpdateListener>> listenersByUser = new ConcurrentHashMap<>();

    public Registration register(Long userId, UI ui, Consumer<PaymentUpdate> listener) {
        PaymentUpdateListener paymentUpdateListener = new PaymentUpdateListener(ui, listener);
        listenersByUser.computeIfAbsent(userId, key -> new CopyOnWriteArrayList<>()).add(paymentUpdateListener);

        return () -> unregister(userId, paymentUpdateListener);
    }

    public void paymentReceived(Long userId, BigDecimal amount, BigDecimal updatedBalance) {
        PaymentUpdate paymentUpdate = new PaymentUpdate(amount, updatedBalance);
        listenersByUser.getOrDefault(userId, List.of()).forEach(listener -> listener.notify(paymentUpdate));
    }

    private void unregister(Long userId, PaymentUpdateListener listener) {
        List<PaymentUpdateListener> listeners = listenersByUser.get(userId);
        if (listeners == null) {
            return;
        }

        listeners.remove(listener);
        if (listeners.isEmpty()) {
            listenersByUser.remove(userId);
        }
    }

    public record PaymentUpdate(BigDecimal amount, BigDecimal updatedBalance) {
    }

    private record PaymentUpdateListener(UI ui, Consumer<PaymentUpdate> listener) {
        private void notify(PaymentUpdate paymentUpdate) {
            ui.access(() -> listener.accept(paymentUpdate));
        }
    }
}
