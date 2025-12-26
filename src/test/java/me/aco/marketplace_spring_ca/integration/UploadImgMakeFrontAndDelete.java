package me.aco.marketplace_spring_ca.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import me.aco.marketplace_spring_ca.infrastructure.persistence.CrudImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;

@SpringBootTest
public class UploadImgMakeFrontAndDelete {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaImageRepository jpaImageRepository;
    @Autowired
    private CrudImageRepository crudImageRepository;

    @Test
    void testUploadImgMakeFrontAndDelete() {
        // Test implementation goes here
    }
    
}
