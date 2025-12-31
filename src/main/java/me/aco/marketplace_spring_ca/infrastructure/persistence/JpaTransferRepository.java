package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransferRepository extends JpaRepository<Transfer, Long> {
    
    @Query("SELECT t FROM PurchaseTransfer t WHERE t.buyer = :buyer")
    List<Transfer> findByBuyer(@Param("buyer") User buyer);
    
    @Query("SELECT t FROM PurchaseTransfer t WHERE t.seller = :seller")
    List<Transfer> findBySeller(@Param("seller") User seller);
    
    @Query("SELECT t FROM Transfer t WHERE TYPE(t) IN (PaymentTransfer, WithdrawalTransfer) " +
           "AND ((TYPE(t) = PaymentTransfer AND TREAT(t AS PaymentTransfer).user = :user) " +
           "OR (TYPE(t) = WithdrawalTransfer AND TREAT(t AS WithdrawalTransfer).user = :user))")
    List<Transfer> findByUser(@Param("user") User user);
}
