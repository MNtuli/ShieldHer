package za.co.shieldher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\s*$|^\\+?[0-9 ()-]{7,20}$", message = "Enter a valid phone number, e.g. 0821234567")
    private String phone;

    @Email(message = "Enter a valid email address")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    private String email;

    @Size(max = 50, message = "Relationship must be 50 characters or fewer")
    private String relationship;
}
