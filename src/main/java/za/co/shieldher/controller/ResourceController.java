package za.co.shieldher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.shieldher.dto.ResourceResponse;
import za.co.shieldher.service.ResourceService;

import java.util.List;

/** Public: someone in distress must be able to read this without logging in. */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    public List<ResourceResponse> list() {
        return resourceService.list();
    }
}
