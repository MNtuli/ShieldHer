package za.co.shieldher.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.shieldher.dto.ResourceResponse;
import za.co.shieldher.model.SupportResource;
import za.co.shieldher.repository.SupportResourceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock SupportResourceRepository repository;

    @Test
    void listMapsEveryResource() {
        when(repository.findAllByOrderByCategoryAscTitleAsc()).thenReturn(List.of(
                SupportResource.builder().id(1L).title("GBV Command Centre").description("24h")
                        .category("HELPLINE").phone("0800 428 428").build()));

        List<ResourceResponse> result = new ResourceService(repository).list();

        assertEquals(1, result.size());
        assertEquals("GBV Command Centre", result.get(0).title());
        assertEquals("0800 428 428", result.get(0).phone());
    }

    @Test
    void listIsEmptyWhenNothingSeeded() {
        when(repository.findAllByOrderByCategoryAscTitleAsc()).thenReturn(List.of());
        assertTrue(new ResourceService(repository).list().isEmpty());
    }
}
