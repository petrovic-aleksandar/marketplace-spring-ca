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
@RequestMapping("/api/Image")
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

    @GetMapping("{itemId}")
    public CompletableFuture<ResponseEntity<List<ImageDto>>> getByItemId(@PathVariable Long itemId) {
        return getImagesByItemQueryHandler.handle(new GetImagesByItemQuery(itemId))
            .thenApply(ResponseEntity::ok);
    }

    @PostMapping(value = "{itemId}", consumes = {"multipart/form-data"})
    public CompletableFuture<ResponseEntity<ImageDto>> add(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new AddImageCommand(itemId, file.getOriginalFilename(), file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read uploaded file", e);
            }
        }).thenCompose(addImageCommandHandler::handle)
          .thenApply(this::created);
    }

    @PostMapping("/front/{imageId}")
    public CompletableFuture<ResponseEntity<ImageDto>> makeImageFront(@PathVariable Long imageId) {
        return makeImangeFrontCommandHandler.handle(new MakeImageFrontCommand(imageId))
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{imageId}")
    public CompletableFuture<ResponseEntity<Void>> delete(@PathVariable Long imageId) {
        return deleteImageCommandHandler.handle(new DeleteImageCommand(imageId))
                .thenApply(this::noContent);
    }
}
