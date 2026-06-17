package com.example.data.repository;

import com.example.data.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsBySessionId(String sessionId);

    Payment findBySessionId(String sessionId);
}
