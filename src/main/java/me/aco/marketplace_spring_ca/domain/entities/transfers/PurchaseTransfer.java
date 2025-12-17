package me.aco.marketplace_spring_ca.domain.entities.transfers;

import jakarta.persistence.*;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.User;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PURCHASE")
public class PurchaseTransfer extends Transfer {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public PurchaseTransfer() {
    }

    public PurchaseTransfer(Long id, BigDecimal amount, User buyer, User seller, Item item) {
        super(id, amount);
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "PurchaseTransfer{" +
                "id=" + getId() +
                ", amount=" + getAmount() +
                ", buyer=" + (buyer != null ? buyer.getId() : null) +
                ", seller=" + (seller != null ? seller.getId() : null) +
                ", item=" + (item != null ? item.getId() : null) +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
