package za.co.shieldher.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContactRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Set<ConstraintViolation<ContactRequest>> check(String name, String phone, String email, String rel) {
        return validator.validate(ContactRequest.builder().name(name).phone(phone).email(email).relationship(rel).build());
    }

    @Test
    void validWithOnlyRequiredFields() {
        assertTrue(check("Thandi", "0829876543", null, null).isEmpty());
    }

    @Test
    void validWithAllFields() {
        assertTrue(check("Thandi", "+27 82 987 6543", "thandi@example.com", "Sister").isEmpty());
    }

    @Test
    void nameIsRequired() {
        assertEquals(1, check("", "0829876543", null, null).size());
        assertEquals(1, check(null, "0829876543", null, null).size());
    }

    @Test
    void phoneIsRequired() {
        assertTrue(check("Thandi", null, null, null).stream().anyMatch(v -> v.getMessage().equals("Phone number is required")));
        assertTrue(check("Thandi", "", null, null).stream().anyMatch(v -> v.getMessage().equals("Phone number is required")));
    }

    @Test
    void phoneMustLookLikeAPhoneNumber() {
        assertEquals(1, check("Thandi", "abc", null, null).size());
        assertEquals(1, check("Thandi", "12345", null, null).size());
    }

    @Test
    void emailIsOptionalButMustBeValidWhenGiven() {
        assertTrue(check("Thandi", "0829876543", "", null).isEmpty());
        assertEquals(1, check("Thandi", "0829876543", "not-an-email", null).size());
    }

    @Test
    void lengthLimits() {
        assertEquals(1, check("a".repeat(101), "0829876543", null, null).size());
        assertEquals(1, check("Thandi", "0829876543", null, "r".repeat(51)).size());
    }
}
