package women_safety_api.auth;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import women_safety_api.auth.dto.RegisterRequest;

@Service
public class FirebaseUserService {

    private final Firestore firestore;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public FirebaseUserService(Firestore firestore) {
        this.firestore = firestore;
    }

    public User createUser(RegisterRequest request) {

        try {

            // Check whether email already exists
            var existingUsers = firestore
                    .collection("users")
                    .whereEqualTo("email", request.email())
                    .get()
                    .get();

            if (!existingUsers.isEmpty()) {
                throw new RuntimeException(
                        "Email is already registered"
                );
            }

            User user = new User();

            String userId = UUID.randomUUID().toString();

            user.setId(userId);
            user.setName(request.name());
            user.setEmail(request.email());

            String hashedPassword =
                    passwordEncoder.encode(request.password());

            user.setPassword(hashedPassword);

            firestore
                    .collection("users")
                    .document(userId)
                    .set(user)
                    .get();

            return user;

        } catch (RuntimeException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create user in Firebase",
                    e
            );
        }
    }

    public User findByEmail(String email) {

        try {

            var snapshot = firestore
                    .collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .get();

            if (snapshot.isEmpty()) {
                return null;
            }

            DocumentSnapshot document =
                    snapshot.getDocuments().get(0);

            return document.toObject(User.class);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to find user in Firebase",
                    e
            );
        }
    }

    public User findById(String userId) {

        try {

            DocumentSnapshot document =
                    firestore
                            .collection("users")
                            .document(userId)
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(User.class);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to find user in Firebase",
                    e
            );
        }
    }
    
    public void updateFcmToken(
            String userId,
            String fcmToken) {

        try {
            firestore
                    .collection("users")
                    .document(userId)
                    .update(
                            "fcmToken",
                            fcmToken
                    )
                    .get();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to save FCM token",
                    e
            );
        }
    }
}