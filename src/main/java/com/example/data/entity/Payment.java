package com.example.data.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sessionId;
    private String paymentId;
    private boolean processed;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    public Payment(AppUser user, BigDecimal amount, boolean processed, String sessionId) {
        this.user = user;
        this.amount = amount;
        this.processed = processed;
        this.sessionId = sessionId;
    }

    public Payment(AppUser user, BigDecimal amount, String sessionId, String paymentId) {
        this.user = user;
        this.amount = amount;
        this.sessionId = sessionId;
        this.paymentId = paymentId;
    }

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
