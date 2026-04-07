package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.AccountPort;
import app.domain.ports.TransferPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchTransfer {

    private final TransferPort transferPort;
    private final AccountPort accountPort;

    @Autowired
    public SearchTransfer(TransferPort transferPort, AccountPort accountPort) {
        this.transferPort = transferPort;
        this.accountPort = accountPort;
    }

    public Transfer findById(Long transferId) throws BusinessException {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("No existe una transferencia con ese ID");
        }
        return transfer;
    }

    // Historial de transferencias por cuenta — cliente y empleados de empresa lo usan
    public List<Transfer> findByAccountNumber(String accountNumber) throws BusinessException {
        if (accountPort.findByAccountNumber(accountNumber) == null) {
            throw new BusinessException("No existe una cuenta con ese número");
        }
        return transferPort.findByAccountNumber(accountNumber);
    }

    // Supervisor: ver todas las transferencias en espera de su empresa
    public List<Transfer> findPendingTransfers() {
        return transferPort.findByStatus(TransferStatus.AWAITING_APPROVAL);
    }
}
