package women_safety_api.auth.dto;

public record LoginResponse(
        String token,
        String message
) {
}