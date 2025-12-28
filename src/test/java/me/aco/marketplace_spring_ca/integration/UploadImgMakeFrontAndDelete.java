package me.aco.marketplace_spring_ca.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.aco.marketplace_spring_ca.application.usecases.auth.command.RegisterCommand;
import me.aco.marketplace_spring_ca.application.usecases.item.command.AddItemCommand;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaItemRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadImgMakeFrontAndDelete {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaImageRepository jpaImageRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaItemRepository jpaItemRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testUploadImgMakeFrontAndDelete() throws Exception {

        // Step 1: Create new User
        RegisterCommand registerCommand = new RegisterCommand(
                "newuser",
                "password123",
                "newuser@example.com",
                "New User",
                "555-9999"
        );

        mockMvc.perform(post("/api/Auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerCommand)))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isCreated()));

        // Assert user created
        var createdUserOpt = jpaUserRepository.findSingleByUsername("newuser");
        assertTrue(createdUserOpt.isPresent());

        // Step 2: Create new Item that user sells
        AddItemCommand addItemCommand = new AddItemCommand(
            "Test Item 1",
            "Description 1",
            99.99,
            1L,
            createdUserOpt.get().getId()
        );

        mockMvc.perform(post("/api/Item")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(addItemCommand)))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isCreated()));

        // Assert item created
        var createdItemOpt = jpaItemRepository.findBySeller(createdUserOpt.get()).stream()
            .filter(i -> i.getName().equals("Test Item 1"))
            .findFirst();
        assertTrue(createdItemOpt.isPresent());

        // Step 3: Upload Image for Item
        String fileName = "test.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

        mockMvc.perform(multipart("/api/Image/" + createdItemOpt.get().getId())
                .file(file)
                .param("fileName", fileName))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isCreated()));

        // Assert image uploaded
        var imagesForItem = jpaImageRepository.findByItemId(createdItemOpt.get().getId());
        assertFalse(imagesForItem.isEmpty());
        var uploadedImage = imagesForItem.get(0);

        // Step 4: Make Image Front
        mockMvc.perform(post("/api/Image/front/" + uploadedImage.getId())
            .param("imageId", String.valueOf(uploadedImage.getId()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isOk()));
        
        // Assert image is front
        var frontImageOpt = jpaImageRepository.findById(uploadedImage.getId());
        assertTrue(frontImageOpt.isPresent());
        assertTrue(frontImageOpt.get().isFront());

        // Step 5: Delete Image
        mockMvc.perform(delete("/api/Image/" + uploadedImage.getId())
            .param("imageId", String.valueOf(uploadedImage.getId()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(request().asyncStarted())
            .andDo(result -> mockMvc.perform(asyncDispatch(result))
            .andExpect(status().isNoContent()));

        // Assert image deleted
        var deletedImageOpt = jpaImageRepository.findById(uploadedImage.getId());
        assertTrue(deletedImageOpt.isEmpty());
    }
    
}
