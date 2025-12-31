package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/Transfer")
@RequiredArgsConstructor
public class TransfersController extends BaseController {

    private final GetTransfersByUserQueryHandler getTransfersByUserQueryHandler;
    private final AddPaymentCommandHandler addPaymentCommandHandler;
    private final AddWithdrawalCommandHandler addWithdrawalCommandHandler;
    private final PurchaseItemCommandHandler purchaseItemCommandHandler;

    @GetMapping("/byUserId/{id}")
    public ResponseEntity<List<TransferDto>> getByUserId(@PathVariable Long id) {
        return ok(getTransfersByUserQueryHandler.handle(new GetTransfersByUserQuery(id)));
    }

    @PostMapping("/payment")
    public ResponseEntity<TransferDto> addPayment(@RequestBody AddPaymentCommand command) {
        return created(addPaymentCommandHandler.handle(command));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransferDto> addWithdrawal(@RequestBody AddWithdrawalCommand command) {
        return created(addWithdrawalCommandHandler.handle(command));
    }

    @PostMapping("/purchase")
    public ResponseEntity<TransferDto> addPurchase(@RequestBody PurchaseItemCommand command) {
        return created(purchaseItemCommandHandler.handle(command));
    }
    
}
