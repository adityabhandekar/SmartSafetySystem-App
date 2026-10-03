package women_safety_api.auth.dto;

public record UserResponse(
    String id,
    String name,
    String email
) {
}