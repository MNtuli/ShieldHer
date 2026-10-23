package za.co.shieldher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.shieldher.model.Alert;
import za.co.shieldher.model.AlertStatus;
import za.co.shieldher.model.DeliveryMode;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertResponse {

    private Long id;
    private String message;
    private Double latitude;
    private Double longitude;
    private AlertStatus status;
    private DeliveryMode deliveryMode;
    private int contactsTotal;
    private int contactsNotified;
    private Instant createdAt;

    /** Maps only safe fields. The owning User is never exposed. */
    public static AlertResponse from(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .message(alert.getMessage())
                .latitude(alert.getLatitude())
                .longitude(alert.getLongitude())
                .status(alert.getStatus())
                .deliveryMode(alert.getDeliveryMode())
                .contactsTotal(alert.getContactsTotal())
                .contactsNotified(alert.getContactsNotified())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}
