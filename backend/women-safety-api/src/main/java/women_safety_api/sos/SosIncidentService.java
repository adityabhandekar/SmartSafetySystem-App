package women_safety_api.sos;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import women_safety_api.sos.dto.CreateSosRequest;
import women_safety_api.sos.dto.UpdateSosRequest;

@Service
public class SosIncidentService {

    private final Firestore firestore;

    public SosIncidentService(Firestore firestore) {
        this.firestore = firestore;
    }

    // CREATE SOS INCIDENT
    public SosIncident createIncident(
            String userId,
            CreateSosRequest request) {

        try {
            SosIncident incident = new SosIncident();

            String incidentId = UUID.randomUUID().toString();

            incident.setId(incidentId);
            incident.setUserId(userId);
            incident.setDeviceId(request.deviceId());
            incident.setTrigger(request.trigger());
            incident.setStatus(SosIncident.IncidentStatus.ACTIVE);
            incident.setStartedAt(Instant.now());
            incident.setFallDetected(false);
            incident.setRiskLevel(SosIncident.RiskLevel.PENDING);

            firestore.collection("sos_incidents")
                    .document(incidentId)
                    .set(incident)
                    .get();

            return incident;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to create SOS incident in Firebase",
                    e
            );
        }
    }

    // GET USER SOS INCIDENTS
    public List<SosIncident> getUserIncidents(String userId) {

        try {
            QuerySnapshot snapshot = firestore
                    .collection("sos_incidents")
                    .whereEqualTo("userId", userId)
                    .get()
                    .get();

            List<SosIncident> incidents = new ArrayList<>();

            for (QueryDocumentSnapshot document : snapshot.getDocuments()) {

                SosIncident incident =
                        document.toObject(SosIncident.class);

                incidents.add(incident);
            }

            return incidents;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to get SOS incidents from Firebase",
                    e
            );
        }
    }

    // UPDATE SOS INCIDENT
    public SosIncident updateIncident(
            String incidentId,
            String userId,
            UpdateSosRequest request) {

        try {
            DocumentSnapshot document = firestore
                    .collection("sos_incidents")
                    .document(incidentId)
                    .get()
                    .get();

            if (!document.exists()) {
                throw new RuntimeException(
                        "SOS incident not found"
                );
            }

            SosIncident incident =
                    document.toObject(SosIncident.class);

            if (incident == null) {
                throw new RuntimeException(
                        "Unable to read SOS incident"
                );
            }

            if (!userId.equals(incident.getUserId())) {
                throw new RuntimeException(
                        "You are not allowed to update this incident"
                );
            }

            incident.setStatus(request.status());

            if (request.status() ==
                    SosIncident.IncidentStatus.RESOLVED
                    || request.status() ==
                    SosIncident.IncidentStatus.CANCELLED) {

                incident.setEndedAt(Instant.now());
            }

            firestore
                    .collection("sos_incidents")
                    .document(incidentId)
                    .set(incident)
                    .get();

            return incident;

        } catch (RuntimeException e) {
            throw e;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to update SOS incident in Firebase",
                    e
            );
        }
    }
}