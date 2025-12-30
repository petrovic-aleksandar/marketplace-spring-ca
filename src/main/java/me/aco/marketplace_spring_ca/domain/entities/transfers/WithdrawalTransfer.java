package me.aco.marketplace_spring_ca.domain.entities.transfers;

import jakarta.persistence.*;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.math.BigDecimal;

@Entity
@Table(name = "withdrawal_transfers")
@DiscriminatorValue("WITHDRAWAL")
public class WithdrawalTransfer extends Transfer {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public WithdrawalTransfer() {
    }

    public WithdrawalTransfer(Long id, BigDecimal amount, User user) {
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
        return "WithdrawalTransfer{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", user=" + (user != null ? user.getId() : null) +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
