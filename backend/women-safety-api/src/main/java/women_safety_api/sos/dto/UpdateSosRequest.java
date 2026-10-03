package women_safety_api.sos.dto;

import jakarta.validation.constraints.NotNull;
import women_safety_api.sos.SosIncident;

public record UpdateSosRequest(
    @NotNull SosIncident.IncidentStatus status
) {}