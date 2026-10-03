package women_safety_api.contact;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TrustedContactRepository
        extends MongoRepository<TrustedContact, String> {

    List<TrustedContact> findByUserId(String userId);
}