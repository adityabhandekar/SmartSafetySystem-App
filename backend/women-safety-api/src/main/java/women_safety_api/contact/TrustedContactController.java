package women_safety_api.contact;

import java.util.List;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import women_safety_api.auth.FirebaseUserService;
import women_safety_api.auth.User;

@RestController
@RequestMapping("/api/v1/contacts")
public class TrustedContactController {

    private final TrustedContactService trustedContactService;
    private final FirebaseUserService firebaseUserService;

    public TrustedContactController(
            TrustedContactService trustedContactService,
            FirebaseUserService firebaseUserService) {

        this.trustedContactService = trustedContactService;
        this.firebaseUserService = firebaseUserService;
    }

    @PostMapping
    public ResponseEntity<TrustedContact> addContact(
            Authentication authentication,
            @RequestBody TrustedContact contact) {

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        TrustedContact savedContact =
                trustedContactService.addContact(
                        user.getId(),
                        contact
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedContact);
    }

    @GetMapping
    public ResponseEntity<List<TrustedContact>> getContacts(
            Authentication authentication) {

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return ResponseEntity.ok(
                trustedContactService.getContacts(
                        user.getId()
                )
        );
    }
    
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteTrustedContact(
            @PathVariable String contactId) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user =
                firebaseUserService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        trustedContactService.deleteContact(
                user.getId(),
                contactId
        );

        return ResponseEntity.noContent().build();
    }
}