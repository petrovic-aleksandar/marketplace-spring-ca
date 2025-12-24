package me.aco.marketplace_spring_ca.application.dto;

import java.math.BigDecimal;

import me.aco.marketplace_spring_ca.domain.entities.transfers.Transfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.WithdrawalTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;

public record TransferDto(
    long id,
    BigDecimal amount,
    String time,
    String type,
    UserDto buyer,
    UserDto seller,
    ItemDto item
){
    public TransferDto(Transfer transfer) {
        this(
            transfer.getId(),
            transfer.getAmount(),
            transfer.getCreatedAt() != null ? transfer.getCreatedAt().toString() : "",
            transfer.getClass().getSimpleName().replace("Transfer", "").toUpperCase(),
            transfer instanceof PurchaseTransfer pt ? new UserDto(pt.getBuyer()) : (transfer instanceof WithdrawalTransfer wt ? new UserDto(wt.getUser()) : null),
            transfer instanceof PurchaseTransfer pt ? new UserDto(pt.getSeller()) : (transfer instanceof PaymentTransfer pyt ? new UserDto(pyt.getUser()) : null),
            transfer instanceof PurchaseTransfer pt ? new ItemDto(pt.getItem()) : null
        );
    }
}
