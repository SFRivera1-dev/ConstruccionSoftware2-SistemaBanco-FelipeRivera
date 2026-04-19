package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerPersonRepository extends JpaRepository<CustomerPersonEntity, Long> {
    CustomerPersonEntity findByDocument(Long document);
}