package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.TransferDto;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddPaymentCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.AddWithdrawalCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommand;
import me.aco.marketplace_spring_ca.application.usecases.transfer.command.PurchaseItemCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.transfer.query.GetTransfersByUserQuery;
import me.aco.marketplace_spring_ca.application.usecases.transfer.query.GetTransfersByUserQueryHandler;

@RestController
@RequestMapping("/api/transfers")
public class TransfersController extends BaseController {

    private final GetTransfersByUserQueryHandler getTransfersByUserQueryHandler;
    private final AddPaymentCommandHandler addPaymentCommandHandler;
    private final AddWithdrawalCommandHandler addWithdrawalCommandHandler;
    private final PurchaseItemCommandHandler purchaseItemCommandHandler;

    public TransfersController(
            GetTransfersByUserQueryHandler getTransfersByUserQueryHandler,
            AddPaymentCommandHandler addPaymentCommandHandler,
            AddWithdrawalCommandHandler addWithdrawalCommandHandler,
            PurchaseItemCommandHandler purchaseItemCommandHandler
    ) {
        this.getTransfersByUserQueryHandler = getTransfersByUserQueryHandler;
        this.addPaymentCommandHandler = addPaymentCommandHandler;
        this.addWithdrawalCommandHandler = addWithdrawalCommandHandler;
        this.purchaseItemCommandHandler = purchaseItemCommandHandler;
    }

    @GetMapping("/user/{id}")
    public CompletableFuture<ResponseEntity<List<TransferDto>>> getByUserId(@PathVariable Long id) {
        return getTransfersByUserQueryHandler.handle(new GetTransfersByUserQuery(id))
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/payment")
    public CompletableFuture<ResponseEntity<TransferDto>> addPayment(@RequestBody AddPaymentCommand command) {
        return addPaymentCommandHandler.handle(command)
            .thenApply(this::created);
    }

    @PostMapping("/withdrawal")
    public CompletableFuture<ResponseEntity<TransferDto>> addWithdrawal(@RequestBody AddWithdrawalCommand command) {
        return addWithdrawalCommandHandler.handle(command)
            .thenApply(this::created);
    }

    @PostMapping("/purchase")
    public CompletableFuture<ResponseEntity<TransferDto>> addPurchase(@RequestBody PurchaseItemCommand command) {
        return purchaseItemCommandHandler.handle(command)
            .thenApply(this::created);
    }
    
}
