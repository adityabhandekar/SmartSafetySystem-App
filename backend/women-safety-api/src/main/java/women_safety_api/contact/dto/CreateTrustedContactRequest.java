package women_safety_api.contact.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTrustedContactRequest(

        @NotBlank
        String name,

        @NotBlank
        String phone,

        String contactUserId

) {
}