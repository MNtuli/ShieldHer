package za.co.shieldher.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String message;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryMode deliveryMode;

    @Column(nullable = false)
    private int contactsTotal;

    @Column(nullable = false)
    private int contactsNotified;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        if (this.status == null) {
            this.status = AlertStatus.PENDING;
        }
    }
}
