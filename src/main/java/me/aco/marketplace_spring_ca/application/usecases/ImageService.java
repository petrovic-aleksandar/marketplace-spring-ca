package me.aco.marketplace_spring_ca.application.usecases;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.application.dto.ImageResponse;
import me.aco.marketplace_spring_ca.domain.entities.Image;
import me.aco.marketplace_spring_ca.domain.entities.Item;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@Service
public class ImageService {

    @Autowired
    private JpaImageRepository imageRepository;

    public Image add(String path, Item item) {
        Image image = new Image();
        image.setPath(path);
        image.setItem(item);
        image.setFront(false);
        return imageRepository.save(image);
    }

    public ImageResponse makeImageFront(Image image, Item item) {
        var currentFrontImage = imageRepository.findByItemAndFrontTrue(item);
        if (currentFrontImage.isPresent()) {
            Image frontImage = currentFrontImage.get();
            frontImage.setFront(false);
            imageRepository.save(frontImage);
        }
        image.setFront(true);
        var updatedImage = imageRepository.save(image);
        return new ImageResponse(updatedImage);
    }

    public void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
			int read;
			byte[] bytes = new byte[1024];
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
