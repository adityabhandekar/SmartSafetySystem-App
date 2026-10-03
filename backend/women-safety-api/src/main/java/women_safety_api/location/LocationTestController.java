package women_safety_api.location;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/location")
public class LocationTestController {

    private final LocationService locationService;

    public LocationTestController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/latest")
    public ResponseEntity<LocationData> getLatestLocation(
            @RequestParam String userId,
            @RequestParam String deviceId) {

        LocationData location =
                locationService.getLatestLocation(
                        userId,
                        deviceId
                );

        if (location == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(location);
    }
}