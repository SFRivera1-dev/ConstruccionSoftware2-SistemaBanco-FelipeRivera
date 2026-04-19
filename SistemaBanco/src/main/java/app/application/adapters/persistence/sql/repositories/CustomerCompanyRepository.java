package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCompanyRepository extends JpaRepository<CustomerCompanyEntity, Long> {
    CustomerCompanyEntity findByDocument(Long document);
}