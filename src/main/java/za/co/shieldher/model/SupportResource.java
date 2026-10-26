package za.co.shieldher.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "support_resources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique so data.sql can be re-run on every start without creating duplicates. */
    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    /** HELPLINE, REPORTING or SAFETY_TIP. */
    @Column(nullable = false)
    private String category;

    private String phone;

    private String url;
}
