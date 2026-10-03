package women_safety_api.location;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/location")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationData> updateLocation(
            @Valid @RequestBody LocationRequest request) {

        LocationData location =
                locationService.saveLocation(request);

        return ResponseEntity.ok(location);
    }
}