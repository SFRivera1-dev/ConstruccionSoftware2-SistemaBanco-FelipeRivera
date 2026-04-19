package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.CreditEntity;
import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends JpaRepository<CreditEntity, Long> {
    List<CreditEntity> findByCustomerPerson(CustomerPersonEntity customerPerson);
    List<CreditEntity> findByCustomerCompany(CustomerCompanyEntity customerCompany);
}