package women_safety_api.alert;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import women_safety_api.alert.dto.DeviceSosRequest;
import women_safety_api.location.LocationData;
import women_safety_api.location.LocationService;
import women_safety_api.sos.SosIncident;
import women_safety_api.sos.SosIncidentService;
import women_safety_api.sos.dto.CreateSosRequest;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceSosController {

    private final SosIncidentService sosIncidentService;
    private final AlertService alertService;
    private final LocationService locationService;

    public DeviceSosController(
            SosIncidentService sosIncidentService,
            AlertService alertService,
            LocationService locationService) {

        this.sosIncidentService = sosIncidentService;
        this.alertService = alertService;
        this.locationService = locationService;
    }

    @PostMapping("/sos")
    public ResponseEntity<SosIncident> deviceSos(
            @Valid @RequestBody DeviceSosRequest request) {

        CreateSosRequest sosRequest =
                new CreateSosRequest(
                        request.deviceId(),
                        SosIncident.TriggerType.BAND_BUTTON
                );

        SosIncident incident =
                sosIncidentService.createIncident(
                        request.userId(),
                        sosRequest
                );

        LocationData latestLocation =
                locationService.getLatestLocation(
                        request.userId(),
                        request.deviceId()
                );

        alertService.sendEmergencyAlert(
                request.userId(),
                request.deviceId(),
                latestLocation
        );

        return ResponseEntity.ok(incident);
    }
}