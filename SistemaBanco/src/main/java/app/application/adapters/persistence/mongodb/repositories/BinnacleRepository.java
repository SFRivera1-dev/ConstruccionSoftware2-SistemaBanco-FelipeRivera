package app.application.adapters.persistence.mongodb.repositories;

import app.application.adapters.persistence.mongodb.documents.BinnacleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BinnacleRepository extends MongoRepository<BinnacleDocument, String> {
    List<BinnacleDocument> findByAffectedProductId(String affectedProductId);
}