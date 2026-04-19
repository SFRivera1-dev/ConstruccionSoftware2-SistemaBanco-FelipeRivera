package app.application.adapters.persistence.sql.repositories;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.domain.models.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {
    List<TransferEntity> findByOriginAccount(BankAccountEntity originAccount);
    List<TransferEntity> findByDestinationAccount(BankAccountEntity destinationAccount);
    List<TransferEntity> findByTransferStatus(TransferStatus transferStatus);
}