package women_safety_api.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import women_safety_api.auth.User;
import women_safety_api.auth.FirebaseUserService;

@RestController
@RequestMapping("/api/v1/notification")
public class FcmTokenController {

    private final FirebaseUserService firebaseUserService;

    public FcmTokenController(
            FirebaseUserService firebaseUserService) {
        this.firebaseUserService =
                firebaseUserService;
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<String> saveFcmToken(
            @Valid @RequestBody FcmTokenRequest request) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        firebaseUserService.updateFcmToken(
                user.getId(),
                request.fcmToken()
        );

        return ResponseEntity.ok(
                "FCM token saved successfully"
        );
    }
}