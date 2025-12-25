package me.aco.marketplace_spring_ca.application.usecases.image.command;

import java.util.concurrent.CompletableFuture;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
@Transactional
public class DeleteImageCommandHandler {

    private final JpaImageRepository imageRepository;

    public DeleteImageCommandHandler(JpaImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public CompletableFuture<Void> handle(DeleteImageCommand command) {
        return CompletableFuture.runAsync(() -> {
            imageRepository.deleteById(command.id());
        });
    }
    
}
