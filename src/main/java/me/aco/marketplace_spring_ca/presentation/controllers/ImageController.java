package me.aco.marketplace_spring_ca.presentation.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.image.command.AddImageCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.AddImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.DeleteImageCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.DeleteImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.MakeImageFrontCommand;
import me.aco.marketplace_spring_ca.application.usecases.image.command.MakeImangeFrontCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQuery;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQueryHandler;
import me.aco.marketplace_spring_ca.infrastructure.security.ImageOwner;
import me.aco.marketplace_spring_ca.infrastructure.security.ImageOwnerByItem;

@RestController
@RequestMapping("/api/Image")
@RequiredArgsConstructor
public class ImageController extends BaseController {

    private final GetImagesByItemQueryHandler getImagesByItemQueryHandler;
    private final AddImageCommandHandler addImageCommandHandler;
    private final MakeImangeFrontCommandHandler makeImangeFrontCommandHandler;
    private final DeleteImageCommandHandler deleteImageCommandHandler;

    @GetMapping("{itemId}")
    public ResponseEntity<List<ImageDto>> getByItemId(@PathVariable Long itemId) {
        return ok(getImagesByItemQueryHandler.handle(new GetImagesByItemQuery(itemId)));
    }

    @PostMapping(value = "{itemId}", consumes = { "multipart/form-data" })
    @ImageOwnerByItem
    public ResponseEntity<ImageDto> add(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) {
        try {
            return created(addImageCommandHandler
                    .handle(new AddImageCommand(itemId, file.getOriginalFilename(), file.getInputStream())));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the uploaded file", e);
        }
    }

    @PostMapping("/front/{imageId}")
    @ImageOwner
    public ResponseEntity<ImageDto> makeImageFront(@PathVariable Long imageId) {
        return ok(makeImangeFrontCommandHandler.handle(new MakeImageFrontCommand(imageId)));
    }

    @DeleteMapping("/{imageId}")
    @ImageOwner
    public ResponseEntity<Void> delete(@PathVariable Long imageId) {
        deleteImageCommandHandler.handle(new DeleteImageCommand(imageId));
        return noContent(null);
    }
}
