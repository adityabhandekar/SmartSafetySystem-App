package women_safety_api.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import women_safety_api.auth.dto.LoginRequest;
import women_safety_api.auth.dto.RegisterRequest;

@Service
public class AuthService {

    private final FirebaseUserService firebaseUserService;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthService(
            FirebaseUserService firebaseUserService,
            JwtService jwtService) {

        this.firebaseUserService = firebaseUserService;
        this.jwtService = jwtService;
    }

    // Register new user in Firebase
    public User registerUser(RegisterRequest request) {

        return firebaseUserService.createUser(request);
    }

    // Login using Firebase user data
    public String loginUser(LoginRequest request) {

        User user =
                firebaseUserService.findByEmail(request.email());

        if (user == null) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        return jwtService.generateToken(user);
    }
}