package me.aco.marketplace_spring_ca.application.usecases;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.application.dto.TransferReq;
import me.aco.marketplace_spring_ca.application.dto.TransferResp;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PaymentTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.PurchaseTransfer;
import me.aco.marketplace_spring_ca.domain.entities.transfers.WithdrawalTransfer;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@Service
@Transactional
public class TransferService {

    @Autowired
    private JpaTransferRepository transferRepository;
    @Autowired
    private JpaUserRepository userRepository;

    public TransferResp addPayment(TransferReq request, User user) {
        PaymentTransfer transfer = new PaymentTransfer();
        transfer.setUser(user);
        transfer.setAmount(BigDecimal.valueOf(request.getAmount()));
        user.addBalance(BigDecimal.valueOf(request.getAmount()));
        transferRepository.save(transfer);
        userRepository.save(user);
        return new TransferResp(transfer);
    }

    public TransferResp addWithdrawal(TransferReq request, User user) {
        WithdrawalTransfer transfer = new WithdrawalTransfer();
        transfer.setUser(user);
        transfer.setAmount(BigDecimal.valueOf(request.getAmount()));
        user.deductBalance(BigDecimal.valueOf(request.getAmount()));
        transferRepository.save(transfer);
        userRepository.save(user);
        return new TransferResp(transfer);
    }

    public TransferResp addPurchase(TransferReq request, User buyer, User seller, Item item) {
        PurchaseTransfer transfer = new PurchaseTransfer();
        transfer.setBuyer(buyer);
        transfer.setSeller(seller);
        transfer.setItem(item);
        transfer.setAmount(BigDecimal.valueOf(request.getAmount()));
        buyer.deductBalance(BigDecimal.valueOf(request.getAmount()));
        seller.addBalance(BigDecimal.valueOf(request.getAmount()));
        transferRepository.save(transfer);
        userRepository.save(buyer);
        userRepository.save(seller);
        return new TransferResp(transfer);
    }
}
