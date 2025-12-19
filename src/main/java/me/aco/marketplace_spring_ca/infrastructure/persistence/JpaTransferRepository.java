package me.aco.marketplace_spring_ca.infrastructure.persistence;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransferRepository extends JpaRepository<Transfer, Long> {
    
    @Query("SELECT t FROM PurchaseTransfer t WHERE t.buyer.id = :buyerId")
    List<Transfer> findByBuyerId(@Param("buyerId") Long buyerId);
    
    @Query("SELECT t FROM PurchaseTransfer t WHERE t.seller.id = :sellerId")
    List<Transfer> findBySellerId(@Param("sellerId") Long sellerId);
    
    @Query("SELECT t FROM Transfer t WHERE TYPE(t) IN (PaymentTransfer, WithdrawalTransfer) " +
           "AND ((TYPE(t) = PaymentTransfer AND TREAT(t AS PaymentTransfer).user.id = :userId) " +
           "OR (TYPE(t) = WithdrawalTransfer AND TREAT(t AS WithdrawalTransfer).user.id = :userId))")
    List<Transfer> findByUserId(@Param("userId") Long userId);
}
