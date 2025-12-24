package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
public class PurchaseItemCommandHandler {

    private final JpaTransferRepository transferRepository;
    private final JpaItemRepository itemRepository;
    private final JpaUserRepository userRepository;

    public PurchaseItemCommandHandler(JpaTransferRepository transferRepository,
                                      JpaItemRepository itemRepository,
                                      JpaUserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public CompletableFuture<TransferDto> handle(PurchaseItemCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            var buyer = userRepository.findById(command.buyerId())
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
            var item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

            BigDecimal price = item.getPrice();
            PurchaseTransfer transfer = new PurchaseTransfer();
            transfer.setBuyer(buyer);
            transfer.setSeller(item.getSeller());
            transfer.setItem(item);
            transfer.setAmount(price);
            buyer.deductBalance(price);
            item.getSeller().addBalance(price);

            transferRepository.save(transfer);
            userRepository.save(buyer);
            userRepository.save(item.getSeller());
            return new TransferDto(transfer);
        });
    }
}
