package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.CustomerPersonEntity;
import app.application.adapters.persistence.sql.entities.CustomerCompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccountEntity, String> {
    BankAccountEntity findByAccountNumber(String accountNumber);
    List<BankAccountEntity> findByCustomerPerson(CustomerPersonEntity customerPerson);
    List<BankAccountEntity> findByCustomerCompany(CustomerCompanyEntity customerCompany);
}