package me.aco.marketplace_spring_ca.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.image.command.AddImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.DeleteImageCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.command.MakeImangeFrontCommandHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQueryHandler;
import me.aco.marketplace_spring_ca.infrastructure.security.JwtTokenService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AddImageCommandHandler addImageCommandHandler;
    @MockitoBean
    private DeleteImageCommandHandler deleteImageCommandHandler;
    @MockitoBean
    private MakeImangeFrontCommandHandler makeImangeFrontCommandHandler;
    @MockitoBean
    private GetImagesByItemQueryHandler getImagesByItemQueryHandler;

    //these two are needed for security context
    @MockitoBean
    private UserDetailsService userDetailsService;
    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    void addImage_returnsImageDto() throws Exception {
        Long itemId = 1L;
        String fileName = "test.jpg";
        MockMultipartFile file = new MockMultipartFile("file", fileName, MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());
        ImageDto dto = new ImageDto(999L, fileName, false);
        when(addImageCommandHandler.handle(any())).thenReturn(dto);

        mockMvc.perform(multipart("/api/Image/" + itemId)
                    .file(file)
                    .param("fileName", fileName))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999L))
                .andExpect(jsonPath("$.path").value(fileName));
    }

    @Test
    void deleteImage_returnsNoContent() throws Exception {

        // Arrange
        Long imageId = 42L;
        doNothing().when(deleteImageCommandHandler).handle(any());

        // Act & Assert
        mockMvc.perform(delete("/api/Image/" + imageId))
              .andExpect(status().isNoContent());
    }

    @Test
    void makeImageFront_returnsImageDto() throws Exception {

        // Arrange
        Long imageId = 5L;
        ImageDto dto = new ImageDto(imageId, "front.jpg", true);
        when(makeImangeFrontCommandHandler.handle(any())).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(post("/api/Image/front/" + imageId))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.front").value(true));
    }
}
