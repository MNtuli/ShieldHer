package za.co.shieldher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.shieldher.dto.ResourceResponse;
import za.co.shieldher.repository.SupportResourceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final SupportResourceRepository repository;

    @Transactional(readOnly = true)
    public List<ResourceResponse> list() {
        return repository.findAllByOrderByCategoryAscTitleAsc()
                .stream()
                .map(ResourceResponse::from)
                .toList();
    }
}
