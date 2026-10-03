package women_safety_api.sos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SosIncidentRepository
        extends MongoRepository<SosIncident, String> {

    List<SosIncident> findByUserIdOrderByStartedAtDesc(String userId);

    List<SosIncident> findByDeviceIdOrderByStartedAtDesc(String deviceId);
}