package za.co.shieldher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.shieldher.model.SupportResource;

import java.util.List;

public interface SupportResourceRepository extends JpaRepository<SupportResource, Long> {

    List<SupportResource> findAllByOrderByCategoryAscTitleAsc();
}
