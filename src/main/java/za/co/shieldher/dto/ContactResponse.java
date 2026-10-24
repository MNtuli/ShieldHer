package za.co.shieldher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import za.co.shieldher.model.EmergencyContact;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String relationship;

    /** Maps only the contract fields. The owning User is never exposed. */
    public static ContactResponse from(EmergencyContact c) {
        return ContactResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .phone(c.getPhone())
                .email(c.getEmail())
                .relationship(c.getRelationship())
                .build();
    }
}
