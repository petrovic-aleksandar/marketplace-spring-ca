package me.aco.marketplace_spring_ca.application.usecases.transfer.command;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.exceptions.BusinessException;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
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

            // Fetch buyer and item with seller
            var buyer = userRepository.findById(command.buyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));
            var item = itemRepository.findById(command.itemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

            // Business validations
            validatePurchase(buyer, item);

            // Process purchase
            PurchaseTransfer transfer = new PurchaseTransfer();
            transfer.setBuyer(buyer);
            transfer.setSeller(item.getSeller());
            transfer.setItem(item);
            transfer.setAmount(item.getPrice());

            // Update balances
            buyer.deductBalance(item.getPrice());
            item.getSeller().addBalance(item.getPrice());

            // Deactivate item
            item.deactivate();

            // Save transfer and update users
            transferRepository.save(transfer);
            userRepository.save(buyer);
            userRepository.save(item.getSeller());
            itemRepository.save(item);

            return new TransferDto(transfer);
        });
    }

    private void validatePurchase(User buyer, Item item) {
        if (!item.isActive())
                throw new BusinessException("Item is not available for purchase");
        if (item.isDeleted())
                throw new BusinessException("Item has been deleted");
        if (buyer.getBalance().compareTo(item.getPrice()) < 0)
                throw new BusinessException("Insufficient balance");
    }
}
