package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.application.adapters.persistence.sql.repositories.TransferRepository;
import app.domain.models.Transfer;
import app.domain.ports.BatchPaymentPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchPaymentPersistenceAdapter implements BatchPaymentPort {

    private final TransferRepository transferRepository;

    public BatchPaymentPersistenceAdapter(TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    @Override
    public void saveAll(List<Transfer> transfers) {
        List<TransferEntity> entities = transfers.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        transferRepository.saveAll(entities);
    }

    @Override
    public List<Transfer> findByCompanyDocument(Long companyDocument) {
        return transferRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private TransferEntity toEntity(Transfer transfer) {
        TransferEntity e = new TransferEntity();
        e.setMount(transfer.getMount());
        e.setCreationDate(transfer.getCreationDate());
        e.setApprovalDate(transfer.getApprovalDate());
        e.setTransferStatus(transfer.getTransferStatus());
        e.setCreatorUserId(transfer.getCreatorUserId());
        e.setApprovedUserId(transfer.getApprovedUserId());
        return e;
    }

    private Transfer toModel(TransferEntity e) {
        Transfer transfer = new Transfer();
        transfer.setIdTransfer(e.getId());
        transfer.setMount(e.getMount());
        transfer.setCreationDate(e.getCreationDate());
        transfer.setApprovalDate(e.getApprovalDate());
        transfer.setTransferStatus(e.getTransferStatus());
        transfer.setCreatorUserId(e.getCreatorUserId());
        transfer.setApprovedUserId(e.getApprovedUserId());
        return transfer;
    }
}