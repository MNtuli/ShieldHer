package za.co.shieldher.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.shieldher.model.Alert;
import za.co.shieldher.model.DeliveryMode;
import za.co.shieldher.model.EmergencyContact;

import java.util.List;

/**
 * Delivers an alert to trusted contacts.
 * MVP: delivery is SIMULATED. Nobody is actually contacted, and the API says so via deliveryMode.
 * Logs contain only the alert id and a count: never names, phone numbers, messages or location.
 */
@Slf4j
@Service
public class NotificationService {

    public DeliveryMode getMode() {
        return DeliveryMode.SIMULATED;
    }

    /** @return how many contacts were notified (simulated: all of them). */
    public int notifyContacts(Alert alert, List<EmergencyContact> contacts) {
        log.info("SIMULATED delivery for alert {}: {} contact(s), no real message sent",
                alert.getId(), contacts.size());
        return contacts.size();
    }
}
