package me.aco.marketplace_spring_ca.application.usecases.image.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteImageCommandHandler {

    private final JpaImageRepository imageRepository;

    public Void handle(DeleteImageCommand command) {
        imageRepository.deleteById(command.id());
        return null;
    }
    
}
