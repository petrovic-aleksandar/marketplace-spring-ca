package me.aco.marketplace_spring_ca.presentation.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.aco.marketplace_spring_ca.application.dto.TransferReq;
import me.aco.marketplace_spring_ca.application.dto.TransferResp;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaTransferRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.application.usecases.TransferService;

@RestController
@RequestMapping("/api/transfers")
public class TransfersController {

    private final JpaTransferRepository transferRepository;
    private final JpaUserRepository userRepository;
    private final JpaItemRepository itemRepository;
    private final TransferService transferService;

    public TransfersController(JpaTransferRepository transferRepository, JpaUserRepository userRepository,
                               JpaItemRepository itemRepository, TransferService transferService) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.transferService = transferService;
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<TransferResp>> getByUserId(@PathVariable Long id) {
        userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<TransferResp> transfers = new ArrayList<>();
        transfers.addAll(transferRepository.findByBuyerId(id).stream()
                .map(TransferResp::new).collect(Collectors.toList()));
        transfers.addAll(transferRepository.findBySellerId(id).stream()
                .map(TransferResp::new).collect(Collectors.toList()));
        
        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/payment")
    public ResponseEntity<TransferResp> addPayment(@RequestBody TransferReq req) {
        var user = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var transferResp = transferService.addPayment(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferResp);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransferResp> addWithdrawal(@RequestBody TransferReq req) {
        var user = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var transferResp = transferService.addWithdrawal(req, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferResp);
    }

    @PostMapping("/purchase")
    public ResponseEntity<TransferResp> addPurchase(@RequestBody TransferReq req) {
        var buyer = userRepository.findById(req.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));
        var seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
        var item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        var transferResp = transferService.addPurchase(req, buyer, seller, item);
        return ResponseEntity.status(HttpStatus.CREATED).body(transferResp);
    }
}
