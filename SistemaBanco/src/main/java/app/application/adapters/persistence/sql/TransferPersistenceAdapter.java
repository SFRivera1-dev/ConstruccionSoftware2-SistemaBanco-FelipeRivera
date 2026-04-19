package app.application.adapters.persistence.sql;

import app.application.adapters.persistence.sql.entities.BankAccountEntity;
import app.application.adapters.persistence.sql.entities.TransferEntity;
import app.application.adapters.persistence.sql.repositories.BankAccountRepository;
import app.application.adapters.persistence.sql.repositories.TransferRepository;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.TransferPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferPersistenceAdapter implements TransferPort {

    private final TransferRepository transferRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public TransferPersistenceAdapter(TransferRepository transferRepository,
                                      BankAccountRepository bankAccountRepository) {
        this.transferRepository = transferRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void save(Transfer transfer) {
        transferRepository.save(toEntity(transfer));
    }

    @Override
    public Transfer findById(Long id) {
        return transferRepository.findById(id)
                .map(this::toModel)
                .orElse(null);
    }

    @Override
    public List<Transfer> findByAccountNumber(String accountNumber) {
        BankAccountEntity accountEntity = bankAccountRepository.findByAccountNumber(accountNumber);
        if (accountEntity == null) return List.of();
        List<Transfer> result = transferRepository.findByOriginAccount(accountEntity)
                .stream().map(this::toModel).collect(Collectors.toList());
        result.addAll(transferRepository.findByDestinationAccount(accountEntity)
                .stream().map(this::toModel).collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<Transfer> findByStatus(TransferStatus status) {
        return transferRepository.findByTransferStatus(status)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(Long transferId, TransferStatus newStatus) {
        transferRepository.findById(transferId).ifPresent(e -> {
            e.setTransferStatus(newStatus);
            transferRepository.save(e);
        });
    }

    private TransferEntity toEntity(Transfer transfer) {
        TransferEntity e = new TransferEntity();
        e.setMount(transfer.getMount());
        e.setCreationDate(transfer.getCreationDate());
        e.setApprovalDate(transfer.getApprovalDate());
        e.setTransferStatus(transfer.getTransferStatus());
        e.setCreatorUserId(transfer.getCreatorUserId());
        e.setApprovedUserId(transfer.getApprovedUserId());
        if (transfer.getOriginAccount() != null) {
            e.setOriginAccount(bankAccountRepository
                    .findByAccountNumber(transfer.getOriginAccount().getAccountNumber()));
        }
        if (transfer.getDestinationAccount() != null) {
            e.setDestinationAccount(bankAccountRepository
                    .findByAccountNumber(transfer.getDestinationAccount().getAccountNumber()));
        }
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
        if (e.getOriginAccount() != null) {
            BankAccount origin = new BankAccount();
            origin.setAccountNumber(e.getOriginAccount().getAccountNumber());
            transfer.setOriginAccount(origin);
        }
        if (e.getDestinationAccount() != null) {
            BankAccount destination = new BankAccount();
            destination.setAccountNumber(e.getDestinationAccount().getAccountNumber());
            transfer.setDestinationAccount(destination);
        }
        return transfer;
    }
}