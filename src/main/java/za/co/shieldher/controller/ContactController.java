package za.co.shieldher.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.shieldher.dto.ContactRequest;
import za.co.shieldher.dto.ContactResponse;
import za.co.shieldher.model.User;
import za.co.shieldher.service.ContactService;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public List<ContactResponse> list(@AuthenticationPrincipal User user) {
        return contactService.list(user);
    }

    @PostMapping
    public ResponseEntity<ContactResponse> create(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody ContactRequest request) {
        return new ResponseEntity<>(contactService.create(user, request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ContactResponse update(@AuthenticationPrincipal User user, @PathVariable Long id,
                                  @Valid @RequestBody ContactRequest request) {
        return contactService.update(user, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        contactService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}
