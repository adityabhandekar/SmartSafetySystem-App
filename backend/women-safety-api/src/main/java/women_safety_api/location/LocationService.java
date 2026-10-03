package women_safety_api.location;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;

@Service
public class LocationService {

    private final Firestore firestore;

    public LocationService(Firestore firestore) {
        this.firestore = firestore;
    }

    public LocationData saveLocation(LocationRequest request) {

        String timestamp = Instant.now().toString();

        LocationData location = new LocationData();

        location.setUserId(request.userId());
        location.setDeviceId(request.deviceId());

        location.setLatitude(request.latitude());
        location.setLongitude(request.longitude());

        location.setAccuracy(request.accuracy());

        location.setTimestamp(timestamp);

        try {

            String locationId = UUID.randomUUID().toString();

            Map<String, Object> locationDocument =
                    new HashMap<>();

            locationDocument.put(
                    "userId",
                    request.userId()
            );

            locationDocument.put(
                    "deviceId",
                    request.deviceId()
            );

            locationDocument.put(
                    "latitude",
                    request.latitude()
            );

            locationDocument.put(
                    "longitude",
                    request.longitude()
            );

            locationDocument.put(
                    "accuracy",
                    request.accuracy()
            );

            locationDocument.put(
                    "timestamp",
                    timestamp
            );

            firestore
                    .collection("locations")
                    .document(locationId)
                    .set(locationDocument)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save location in Firebase",
                    e
            );
        }

        return location;
    }
    
    public LocationData getLatestLocation(
            String userId,
            String deviceId) {

        try {

            var snapshot = firestore
                    .collection("locations")
                    .whereEqualTo("userId", userId)
                    .get()
                    .get();

            LocationData latestLocation = null;

            for (var document : snapshot.getDocuments()) {

                LocationData location =
                        document.toObject(LocationData.class);

                if (location == null) {
                    continue;
                }

                if (!deviceId.equals(location.getDeviceId())) {
                    continue;
                }

                if (latestLocation == null
                        || location.getTimestamp()
                            .compareTo(latestLocation.getTimestamp()) > 0) {

                    latestLocation = location;
                }
            }

            return latestLocation;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to get latest location from Firebase",
                    e
            );
        }
    }
}