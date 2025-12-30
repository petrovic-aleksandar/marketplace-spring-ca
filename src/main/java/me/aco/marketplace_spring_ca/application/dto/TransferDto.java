package me.aco.marketplace_spring_ca.application.dto;

import java.math.BigDecimal;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.WithdrawalTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;

public record TransferDto(
    Long id,
    BigDecimal amount,
    String time,
    String type,
    String buyer,
    String seller,
    String item
){
    public TransferDto(Transfer transfer) {
        this(
            transfer.getId(),
            transfer.getAmount(),
            transfer.getCreatedAt() != null ? transfer.getCreatedAt().toString() : "",
            transfer.getClass().getSimpleName().replace("Transfer", "").toUpperCase(),
            transfer instanceof PurchaseTransfer pt ? pt.getBuyer().getName() : (transfer instanceof WithdrawalTransfer wt ? wt.getUser().getName() : null),
            transfer instanceof PurchaseTransfer pt ? pt.getSeller().getName() : (transfer instanceof PaymentTransfer pyt ? pyt.getUser().getName() : null),
            transfer instanceof PurchaseTransfer pt ? pt.getItem().getName() : null
        );
    }
}
