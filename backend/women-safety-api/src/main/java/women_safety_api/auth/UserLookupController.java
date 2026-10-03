package women_safety_api.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserLookupController {

    private final FirebaseUserService firebaseUserService;

    public UserLookupController(
            FirebaseUserService firebaseUserService) {

        this.firebaseUserService = firebaseUserService;
    }

    @GetMapping("/lookup")
    public ResponseEntity<UserLookupResponse> lookupUser(
            @RequestParam String email) {

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new UserLookupResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        );
    }

    public record UserLookupResponse(
            String userId,
            String name,
            String email
    ) {
    }
}