package me.aco.marketplace_spring_ca.presentation.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import me.aco.marketplace_spring_ca.application.dto.ImageResponse;
import me.aco.marketplace_spring_ca.application.exceptions.ResourceNotFoundException;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.application.usecases.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController extends BaseController {

    private final JpaImageRepository imageRepository;
    private final JpaItemRepository itemRepository;
    private final ImageService imageService;

    public ImageController(JpaImageRepository imageRepository, JpaItemRepository itemRepository,
                          ImageService imageService) {
        this.imageRepository = imageRepository;
        this.itemRepository = itemRepository;
        this.imageService = imageService;
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ImageResponse>> getImagesByItemId(@PathVariable Long itemId) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        var images = item.getImages();
        var resp = images.stream().map(ImageResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/item/{itemId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ImageResponse> add(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) {
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
        
        String imagesDir = System.getProperty("user.home") + File.separator + "marketplace-images" + File.separator;
        String imageDir = imagesDir + item.getSeller().getId() + File.separator + item.getId() + File.separator;
        String uploadedFileLocation = imageDir + file.getOriginalFilename();
        
        try {
            Files.createDirectories(java.nio.file.Path.of(imageDir));
            File objFile = new File(uploadedFileLocation);
            if (objFile.exists()) {
                objFile.delete();
            }
            imageService.saveToFile(file.getInputStream(), uploadedFileLocation);
            Image image = imageService.add(file.getOriginalFilename(), item);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ImageResponse(image));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/front/{imageId}")
    public ResponseEntity<ImageResponse> makeImageFront(@PathVariable Long imageId) {
        var image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        var updatedImage = imageService.makeImageFront(image, image.getItem());
        return ResponseEntity.ok(updatedImage);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image not found");
        }
        imageRepository.deleteById(imageId);
        return ResponseEntity.noContent().build();
    }
}
