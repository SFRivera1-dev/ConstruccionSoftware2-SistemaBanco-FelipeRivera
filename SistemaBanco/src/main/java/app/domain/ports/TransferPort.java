package app.domain.ports;

import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import java.util.List;

public interface TransferPort {
    void save(Transfer transfer);
    Transfer findById(Long id);
    List<Transfer> findByAccountNumber(String accountNumber);
    List<Transfer> findByStatus(TransferStatus status);
    void updateStatus(Long transferId, TransferStatus newStatus);
}
