package za.co.shieldher.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.shieldher.dto.AlertRequest;
import za.co.shieldher.dto.AlertResponse;
import za.co.shieldher.model.Alert;
import za.co.shieldher.model.AlertStatus;
import za.co.shieldher.model.EmergencyContact;
import za.co.shieldher.model.User;
import za.co.shieldher.repository.AlertRepository;
import za.co.shieldher.repository.EmergencyContactRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    static final String DEFAULT_MESSAGE = "I need help. Please contact me as soon as possible.";

    private final AlertRepository alertRepository;
    private final EmergencyContactRepository contactRepository;
    private final NotificationService notificationService;

    @Transactional
    public AlertResponse createAlert(User user, AlertRequest request) {
        boolean hasLat = request.getLatitude() != null;
        boolean hasLng = request.getLongitude() != null;
        if (hasLat != hasLng) {
            throw new IllegalArgumentException("Send both latitude and longitude, or neither");
        }

        String message = request.getMessage() == null ? "" : request.getMessage().trim();
        if (message.isEmpty()) {
            message = DEFAULT_MESSAGE;
        }

        List<EmergencyContact> contacts = contactRepository.findByUserId(user.getId());

        // Store first as PENDING so the alert is recorded before delivery is attempted.
        Alert alert = alertRepository.save(Alert.builder()
                .user(user)
                .message(message)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(AlertStatus.PENDING)
                .deliveryMode(notificationService.getMode())
                .contactsTotal(contacts.size())
                .contactsNotified(0)
                .build());

        if (contacts.isEmpty()) {
            alert.setStatus(AlertStatus.NO_CONTACTS);
        } else {
            try {
                int notified = notificationService.notifyContacts(alert, contacts);
                alert.setContactsNotified(notified);
                alert.setStatus(notified > 0 ? AlertStatus.SENT : AlertStatus.FAILED);
            } catch (Exception ex) {
                log.error("Delivery failed for alert {}: {}", alert.getId(), ex.getClass().getSimpleName());
                alert.setContactsNotified(0);
                alert.setStatus(AlertStatus.FAILED);
            }
        }

        return AlertResponse.from(alertRepository.save(alert));
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getMyAlerts(User user) {
        return alertRepository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId())
                .stream()
                .map(AlertResponse::from)
                .toList();
    }
}
