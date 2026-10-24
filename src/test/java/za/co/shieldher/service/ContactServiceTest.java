package za.co.shieldher.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.shieldher.dto.ContactRequest;
import za.co.shieldher.dto.ContactResponse;
import za.co.shieldher.exception.ResourceNotFoundException;
import za.co.shieldher.model.EmergencyContact;
import za.co.shieldher.model.User;
import za.co.shieldher.repository.EmergencyContactRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock EmergencyContactRepository repository;

    ContactService service;
    User user;

    @BeforeEach
    void setUp() {
        service = new ContactService(repository);
        user = User.builder().id(1L).email("a@b.co.za").build();
        lenient().when(repository.save(any(EmergencyContact.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private EmergencyContact contact(long id, String name) {
        return EmergencyContact.builder().id(id).user(user).name(name).phone("0821234567").build();
    }

    @Test
    void create_trimsFields_andTurnsBlankOptionalsIntoNull() {
        ContactResponse r = service.create(user, ContactRequest.builder()
                .name("  Thandi ").phone(" 0829876543 ").email("   ").relationship("").build());

        assertEquals("Thandi", r.getName());
        assertEquals("0829876543", r.getPhone());
        assertNull(r.getEmail());
        assertNull(r.getRelationship());
    }

    @Test
    void create_attachesContactToTheLoggedInUser() {
        service.create(user, ContactRequest.builder().name("Thandi").phone("0829876543").build());

        org.mockito.ArgumentCaptor<EmergencyContact> captor = org.mockito.ArgumentCaptor.forClass(EmergencyContact.class);
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getUser().getId());
    }

    @Test
    void list_isSortedByNameIgnoringCase() {
        when(repository.findByUserId(1L)).thenReturn(List.of(contact(1, "zola"), contact(2, "Anele"), contact(3, "bongi")));

        List<String> names = service.list(user).stream().map(ContactResponse::getName).toList();

        assertEquals(List.of("Anele", "bongi", "zola"), names);
    }

    @Test
    void list_emptyWhenUserHasNoContacts() {
        when(repository.findByUserId(1L)).thenReturn(List.of());

        assertEquals(0, service.list(user).size());
    }

    @Test
    void update_changesMyContact() {
        when(repository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(contact(7, "Old")));

        ContactResponse r = service.update(user, 7L,
                ContactRequest.builder().name("New").phone("0820000000").relationship("Sister").build());

        assertEquals("New", r.getName());
        assertEquals("Sister", r.getRelationship());
    }

    @Test
    void update_someoneElsesContact_isNotFound_andNothingSaved() {
        when(repository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(user, 7L,
                ContactRequest.builder().name("X").phone("0820000000").build()));
        verify(repository, never()).save(any());
    }

    @Test
    void delete_myContact_deletesIt() {
        EmergencyContact mine = contact(7, "Thandi");
        when(repository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(mine));

        service.delete(user, 7L);

        verify(repository).delete(mine);
    }

    @Test
    void delete_someoneElsesContact_isNotFound_andNothingDeleted() {
        when(repository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(user, 7L));
        verify(repository, never()).delete(any());
    }
}
