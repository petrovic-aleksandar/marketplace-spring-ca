package me.aco.marketplace_spring_ca.domain.entities.transfers;

import jakarta.persistence.*;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PAYMENT")
public class PaymentTransfer extends Transfer {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PaymentTransfer() {
    }

    public PaymentTransfer(Long id, BigDecimal amount, User user) {
        super(id, amount);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "PaymentTransfer{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", user=" + (user != null ? user.getId() : null) +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
