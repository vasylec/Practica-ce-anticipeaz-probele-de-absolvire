package com.example.service;

import com.example.data.entity.Payment;
import com.example.data.exception.SessionExistsException;
import com.example.data.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public boolean existsBySessionId(String sessionId) {
        return paymentRepository.existsBySessionId(sessionId);
    }

    @Transactional
    public void processPayment(Payment payment) throws SessionExistsException {
        if(existsBySessionId(payment.getSessionId())){
            throw new SessionExistsException("Session already exists");
        }

        save(payment);
    }

    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPayment(String sessionId){
        return paymentRepository.findBySessionId(sessionId);
    }
}
