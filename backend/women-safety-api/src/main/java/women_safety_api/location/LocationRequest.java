package women_safety_api.location;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(

        @NotBlank
        String userId,

        @NotBlank
        String deviceId,

        double latitude,

        double longitude,

        Double accuracy

) {}