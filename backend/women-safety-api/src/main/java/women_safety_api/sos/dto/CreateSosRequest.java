package women_safety_api.sos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import women_safety_api.sos.SosIncident;

public record CreateSosRequest(

        @NotBlank
        String deviceId,

        @NotNull
        SosIncident.TriggerType trigger

) {
}