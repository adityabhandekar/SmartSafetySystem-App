package women_safety_api.sos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import women_safety_api.auth.FirebaseUserService;
import women_safety_api.auth.User;
import women_safety_api.sos.dto.CreateSosRequest;
import women_safety_api.sos.dto.UpdateSosRequest;

@RestController
@RequestMapping("/api/v1/sos")
public class SosIncidentController {

    private final SosIncidentService sosIncidentService;
    private final FirebaseUserService firebaseUserService;

    public SosIncidentController(
            SosIncidentService sosIncidentService,
            FirebaseUserService firebaseUserService) {

        this.sosIncidentService = sosIncidentService;
        this.firebaseUserService = firebaseUserService;
    }

    @PostMapping
    public ResponseEntity<SosIncident> createSos(
            Authentication authentication,
            @Valid @RequestBody CreateSosRequest request) {

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        SosIncident createdIncident =
                sosIncidentService.createIncident(
                        user.getId(),
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdIncident);
    }

    @GetMapping
    public ResponseEntity<List<SosIncident>> getUserIncidents(
            Authentication authentication) {

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return ResponseEntity.ok(
                sosIncidentService.getUserIncidents(
                        user.getId()
                )
        );
    }

    @PatchMapping("/{incidentId}")
    public ResponseEntity<SosIncident> updateSos(
            @PathVariable String incidentId,
            Authentication authentication,
            @Valid @RequestBody UpdateSosRequest request) {

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        SosIncident updatedIncident =
                sosIncidentService.updateIncident(
                        incidentId,
                        user.getId(),
                        request
                );

        return ResponseEntity.ok(updatedIncident);
    }
}