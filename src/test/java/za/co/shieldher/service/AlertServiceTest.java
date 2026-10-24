package za.co.shieldher.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.shieldher.dto.AlertRequest;
import za.co.shieldher.dto.AlertResponse;
import za.co.shieldher.model.Alert;
import za.co.shieldher.model.AlertStatus;
import za.co.shieldher.model.DeliveryMode;
import za.co.shieldher.model.EmergencyContact;
import za.co.shieldher.model.User;
import za.co.shieldher.repository.AlertRepository;
import za.co.shieldher.repository.EmergencyContactRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock AlertRepository alertRepository;
    @Mock EmergencyContactRepository contactRepository;
    @Mock NotificationService notificationService;

    AlertService service;
    User user;

    @BeforeEach
    void setUp() {
        service = new AlertService(alertRepository, contactRepository, notificationService);
        user = User.builder().id(1L).email("a@b.co.za").build();
        lenient().when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(notificationService.getMode()).thenReturn(DeliveryMode.SIMULATED);
    }

    private List<EmergencyContact> contacts(int n) {
        return java.util.stream.IntStream.range(0, n)
                .mapToObj(i -> EmergencyContact.builder().name("C" + i).phone("082000000" + i).build())
                .toList();
    }

    @Test
    void noContacts_isNoContacts_andNobodyIsNotified() {
        when(contactRepository.findByUserId(1L)).thenReturn(List.of());

        AlertResponse r = service.createAlert(user, new AlertRequest());

        assertEquals(AlertStatus.NO_CONTACTS, r.getStatus());
        assertEquals(0, r.getContactsTotal());
        assertEquals(0, r.getContactsNotified());
        verify(notificationService, never()).notifyContacts(any(), anyList());
    }

    @Test
    void withContacts_isSent_andReportsRealCounts() {
        when(contactRepository.findByUserId(1L)).thenReturn(contacts(3));
        when(notificationService.notifyContacts(any(), anyList())).thenReturn(3);

        AlertResponse r = service.createAlert(user, new AlertRequest());

        assertEquals(AlertStatus.SENT, r.getStatus());
        assertEquals(3, r.getContactsTotal());
        assertEquals(3, r.getContactsNotified());
        assertEquals(DeliveryMode.SIMULATED, r.getDeliveryMode());
    }

    @Test
    void partialDelivery_reportsOnlyWhatWasNotified() {
        when(contactRepository.findByUserId(1L)).thenReturn(contacts(3));
        when(notificationService.notifyContacts(any(), anyList())).thenReturn(2);

        AlertResponse r = service.createAlert(user, new AlertRequest());

        assertEquals(AlertStatus.SENT, r.getStatus());
        assertEquals(2, r.getContactsNotified());
        assertEquals(3, r.getContactsTotal());
    }

    @Test
    void deliveryThrows_isFailed_butStillSaved() {
        when(contactRepository.findByUserId(1L)).thenReturn(contacts(2));
        when(notificationService.notifyContacts(any(), anyList())).thenThrow(new RuntimeException("boom"));

        AlertResponse r = service.createAlert(user, new AlertRequest());

        assertEquals(AlertStatus.FAILED, r.getStatus());
        assertEquals(0, r.getContactsNotified());
    }

    @Test
    void zeroNotified_isFailed() {
        when(contactRepository.findByUserId(1L)).thenReturn(contacts(2));
        when(notificationService.notifyContacts(any(), anyList())).thenReturn(0);

        assertEquals(AlertStatus.FAILED, service.createAlert(user, new AlertRequest()).getStatus());
    }

    @Test
    void emptyMessage_usesDefault() {
        when(contactRepository.findByUserId(1L)).thenReturn(List.of());

        AlertResponse r = service.createAlert(user, AlertRequest.builder().message("   ").build());

        assertEquals(AlertService.DEFAULT_MESSAGE, r.getMessage());
    }

    @Test
    void latitudeWithoutLongitude_isRejected_andNothingSaved() {
        AlertRequest req = AlertRequest.builder().latitude(-26.2).build();

        assertThrows(IllegalArgumentException.class, () -> service.createAlert(user, req));
        verify(alertRepository, never()).save(any());
    }

    @Test
    void bothCoordinates_areStored() {
        when(contactRepository.findByUserId(1L)).thenReturn(List.of());

        AlertResponse r = service.createAlert(user,
                AlertRequest.builder().latitude(-26.2041).longitude(28.0473).build());

        assertEquals(-26.2041, r.getLatitude());
        assertEquals(28.0473, r.getLongitude());
    }

    @Test
    void myAlerts_onlyQueriesForThisUser() {
        when(alertRepository.findByUserIdOrderByCreatedAtDescIdDesc(1L)).thenReturn(List.of());

        assertEquals(0, service.getMyAlerts(user).size());
        verify(alertRepository).findByUserIdOrderByCreatedAtDescIdDesc(1L);
        verify(alertRepository, never()).findAll();
    }
}
