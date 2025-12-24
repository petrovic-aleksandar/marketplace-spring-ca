package me.aco.marketplace_spring_ca.presentation.controllers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.image.command.AddImageCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.AddImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.DeleteImageCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.DeleteImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.MakeImageFrontCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.MakeImangeFrontCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQuery;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQueryHandler;

@RestController
@RequestMapping("/api/images")
public class ImageController extends BaseController {

    private final GetImagesByItemQueryHandler getImagesByItemQueryHandler;
    private final AddImageCommandHandler addImageCommandHandler;
    private final MakeImangeFrontCommandHandler makeImangeFrontCommandHandler;
    private final DeleteImageCommandHandler deleteImageCommandHandler;

    public ImageController(
            GetImagesByItemQueryHandler getImagesByItemQueryHandler,
            AddImageCommandHandler addImageCommandHandler,
            MakeImangeFrontCommandHandler makeImangeFrontCommandHandler,
            DeleteImageCommandHandler deleteImageCommandHandler
    ) {
        this.getImagesByItemQueryHandler = getImagesByItemQueryHandler;
        this.addImageCommandHandler = addImageCommandHandler;
        this.makeImangeFrontCommandHandler = makeImangeFrontCommandHandler;
        this.deleteImageCommandHandler = deleteImageCommandHandler;
    }

    @GetMapping("/item/{itemId}")
    public CompletableFuture<ResponseEntity<List<ImageDto>>> getByItemId(@PathVariable Long itemId) {
        return getImagesByItemQueryHandler.handle(new GetImagesByItemQuery(itemId))
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping(value = "/item/{itemId}", consumes = {"multipart/form-data"})
    public CompletableFuture<ResponseEntity<ImageDto>> add(@PathVariable AddImageCommand command, @RequestParam("file") MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                AddImageCommand commandWithStream = AddImageCommand.withStream(file.getInputStream(), command);
                return commandWithStream;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read uploaded file", e);
            }
        }).thenCompose(cmd -> addImageCommandHandler.handle(cmd))
          .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/front/{imageId}")
    public CompletableFuture<ResponseEntity<ImageDto>> makeImageFront(@PathVariable MakeImageFrontCommand command) {
        return makeImangeFrontCommandHandler.handle(command)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{imageId}")
    public CompletableFuture<ResponseEntity<Void>> delete(@PathVariable DeleteImageCommand command) {
        return deleteImageCommandHandler.handle(command)
                .thenApply(this::noContent);
    }
}
