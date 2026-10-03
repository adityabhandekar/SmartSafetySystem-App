package women_safety_api.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import women_safety_api.auth.dto.RegisterRequest;
import women_safety_api.auth.dto.UserResponse;
import women_safety_api.auth.dto.LoginRequest;
import women_safety_api.auth.dto.LoginResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

    	User registeredUser = authService.registerUser(request);

    	UserResponse response = new UserResponse(
    	        registeredUser.getId(),
    	        registeredUser.getName(),
    	        registeredUser.getEmail()
    	);

    	return ResponseEntity
    	        .status(HttpStatus.CREATED)
    	        .body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.loginUser(request);

        LoginResponse response = new LoginResponse(
                token,
                "Login successful"
        );

        return ResponseEntity.ok(response);
    }
}