package women_safety_api.alert.dto;

import jakarta.validation.constraints.NotBlank;

public record DeviceSosRequest(

        @NotBlank
        String userId,

        @NotBlank
        String deviceId

) {
}