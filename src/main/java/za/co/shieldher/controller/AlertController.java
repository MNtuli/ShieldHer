package za.co.shieldher.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.shieldher.dto.AlertRequest;
import za.co.shieldher.dto.AlertResponse;
import za.co.shieldher.model.User;
import za.co.shieldher.service.AlertService;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertResponse> create(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody(required = false) AlertRequest request) {
        AlertRequest body = request != null ? request : new AlertRequest();
        return new ResponseEntity<>(alertService.createAlert(user, body), HttpStatus.CREATED);
    }

    @GetMapping
    public List<AlertResponse> mine(@AuthenticationPrincipal User user) {
        return alertService.getMyAlerts(user);
    }
}
