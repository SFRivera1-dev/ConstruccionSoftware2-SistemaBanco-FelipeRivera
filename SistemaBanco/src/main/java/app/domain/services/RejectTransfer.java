package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Binnacle;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.BinnaclePort;
import app.domain.ports.TransferPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class RejectTransfer {

    private final TransferPort transferPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public RejectTransfer(TransferPort transferPort, BinnaclePort binnaclePort) {
        this.transferPort = transferPort;
        this.binnaclePort = binnaclePort;
    }

    public void rejectTransfer(Long transferId, Long rejectorUserId, Role rejectorRole) throws BusinessException {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("No existe una transferencia con ese ID");
        }

        // Regla: solo se puede rechazar si está en espera de aprobación
        if (transfer.getTransferStatus() != TransferStatus.AWAITING_APPROVAL) {
            throw new BusinessException("Solo se pueden rechazar transferencias en estado 'En espera de aprobación'");
        }

        transferPort.updateStatus(transferId, TransferStatus.REJECT);

        // Bitácora
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Transferencia_Rechazada");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(rejectorUserId);
        binnacle.setRoleUser(rejectorRole);
        binnacle.setAffectedProductId(String.valueOf(transferId));
        binnaclePort.save(binnacle);
    }
}
