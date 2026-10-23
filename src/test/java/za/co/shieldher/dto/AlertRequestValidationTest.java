package za.co.shieldher.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlertRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void emptyRequest_isValid() {
        assertTrue(validator.validate(new AlertRequest()).isEmpty());
    }

    @Test
    void message500_isValid_message501_isNot() {
        assertTrue(validator.validate(AlertRequest.builder().message("a".repeat(500)).build()).isEmpty());
        assertEquals(1, validator.validate(AlertRequest.builder().message("a".repeat(501)).build()).size());
    }

    @Test
    void latitudeAndLongitudeBounds() {
        assertTrue(validator.validate(AlertRequest.builder().latitude(90.0).longitude(-180.0).build()).isEmpty());
        assertEquals(1, validator.validate(AlertRequest.builder().latitude(90.1).longitude(0.0).build()).size());
        assertEquals(1, validator.validate(AlertRequest.builder().latitude(0.0).longitude(180.1).build()).size());
    }
}
