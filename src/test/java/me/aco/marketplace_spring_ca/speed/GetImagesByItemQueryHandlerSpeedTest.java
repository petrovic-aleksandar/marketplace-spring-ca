package me.aco.marketplace_spring_ca.speed;

import me.aco.marketplace_spring_ca.application.dto.ImageDto;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQuery;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQueryHandler;
import me.aco.marketplace_spring_ca.application.usecases.image.query.GetImagesByItemQueryHandler_JPA;
import me.aco.marketplace_spring_ca.infrastructure.persistence.CrudImageRepository;
import me.aco.marketplace_spring_ca.infrastructure.persistence.JpaImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetImagesByItemQueryHandlerSpeedTest {
    private JpaImageRepository jpaRepo;
    private CrudImageRepository crudRepo;
    private GetImagesByItemQueryHandler_JPA jpaHandler;
    private GetImagesByItemQueryHandler crudHandler;

    @BeforeEach
    void setUp() {
        jpaRepo = mock(JpaImageRepository.class);
        crudRepo = mock(CrudImageRepository.class);
        jpaHandler = new GetImagesByItemQueryHandler_JPA(jpaRepo);
        crudHandler = new GetImagesByItemQueryHandler(crudRepo);
    }

    @Test
    void compareSpeed() throws ExecutionException, InterruptedException {
        // Prepare a large list of mock images
        List<me.aco.marketplace_spring_ca.domain.entities.Image> images =
                IntStream.range(0, 10000)
                        .mapToObj(i -> mock(me.aco.marketplace_spring_ca.domain.entities.Image.class))
                        .toList();
        when(jpaRepo.findByItemId(1L)).thenReturn(images);
        when(crudRepo.findByItemId(1L)).thenReturn(images);

        GetImagesByItemQuery query = mock(GetImagesByItemQuery.class);
        when(query.itemId()).thenReturn(1L);

        long startJpa = System.nanoTime();
        List<ImageDto> jpaResult = jpaHandler.handle(query).get();
        long endJpa = System.nanoTime();

        long startCrud = System.nanoTime();
        List<ImageDto> crudResult = crudHandler.handle(query).get();
        long endCrud = System.nanoTime();

        System.out.printf("JPA Handler: %d ms\n", (endJpa - startJpa) / 1_000_000);
        System.out.printf("CRUD Handler: %d ms\n", (endCrud - startCrud) / 1_000_000);

        assertEquals(jpaResult.size(), crudResult.size());
    }
}
