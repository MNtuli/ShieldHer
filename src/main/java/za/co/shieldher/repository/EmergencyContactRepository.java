package za.co.shieldher.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.shieldher.model.EmergencyContact;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    List<EmergencyContact> findByUserId(Long userId);

    /** Ownership-safe lookup: a contact that belongs to someone else is simply "not found". */
    Optional<EmergencyContact> findByIdAndUserId(Long id, Long userId);
}
