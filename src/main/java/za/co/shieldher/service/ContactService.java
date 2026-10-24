package za.co.shieldher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.shieldher.dto.ContactRequest;
import za.co.shieldher.dto.ContactResponse;
import za.co.shieldher.exception.ResourceNotFoundException;
import za.co.shieldher.model.EmergencyContact;
import za.co.shieldher.model.User;
import za.co.shieldher.repository.EmergencyContactRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    static final String NOT_FOUND = "Contact not found";

    private final EmergencyContactRepository contactRepository;

    @Transactional(readOnly = true)
    public List<ContactResponse> list(User user) {
        return contactRepository.findByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(EmergencyContact::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(EmergencyContact::getId))
                .map(ContactResponse::from)
                .toList();
    }

    @Transactional
    public ContactResponse create(User user, ContactRequest request) {
        EmergencyContact contact = EmergencyContact.builder().user(user).build();
        apply(contact, request);
        return ContactResponse.from(contactRepository.save(contact));
    }

    @Transactional
    public ContactResponse update(User user, Long id, ContactRequest request) {
        EmergencyContact contact = findMine(user, id);
        apply(contact, request);
        return ContactResponse.from(contactRepository.save(contact));
    }

    @Transactional
    public void delete(User user, Long id) {
        contactRepository.delete(findMine(user, id));
    }

    private EmergencyContact findMine(User user, Long id) {
        return contactRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND));
    }

    private void apply(EmergencyContact contact, ContactRequest request) {
        contact.setName(request.getName().trim());
        contact.setPhone(request.getPhone().trim());
        contact.setEmail(blankToNull(request.getEmail()));
        contact.setRelationship(blankToNull(request.getRelationship()));
    }

    private String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
