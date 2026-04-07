package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatement;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.AccountPort;
import app.domain.ports.BinnaclePort;
import app.domain.ports.TransferPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ApproveTransfer {

    private final TransferPort transferPort;
    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public ApproveTransfer(TransferPort transferPort, AccountPort accountPort, BinnaclePort binnaclePort) {
        this.transferPort = transferPort;
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void approveTransfer(Long transferId, Long approverUserId, Role approverRole) throws BusinessException {
        Transfer transfer = transferPort.findById(transferId);
        if (transfer == null) {
            throw new BusinessException("No existe una transferencia con ese ID");
        }

        // Regla: solo se puede aprobar si está en espera de aprobación
        if (transfer.getTransferStatus() != TransferStatus.AWAITING_APPROVAL) {
            throw new BusinessException("Solo se pueden aprobar transferencias en estado 'En espera de aprobación'");
        }

        // Regla de vencimiento: si lleva más de 60 minutos en espera → VENCIDA
        Instant creationInstant = transfer.getCreationDate().toInstant();
        long minutesWaiting = ChronoUnit.MINUTES.between(creationInstant, Instant.now());
        if (minutesWaiting > 60) {
            transferPort.updateStatus(transferId, TransferStatus.EXPIRED);

            Binnacle expiredBinnacle = new Binnacle();
            expiredBinnacle.setOperationType("Transferencia_Vencida");
            expiredBinnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
            expiredBinnacle.setIdUser(approverUserId);
            expiredBinnacle.setRoleUser(approverRole);
            expiredBinnacle.setAffectedProductId(String.valueOf(transferId));
            binnaclePort.save(expiredBinnacle);

            throw new BusinessException("La transferencia ha vencido por falta de aprobación en el tiempo establecido");
        }

        // Regla: validar saldo suficiente en cuenta origen al momento de aprobar
        BankAccount originAccount = accountPort.findByAccountNumber(
                transfer.getOriginAccount().getAccountNumber());
        if (originAccount == null) {
            throw new BusinessException("La cuenta origen no existe");
        }
        if (originAccount.getAccountStatement() == AccountStatement.BLOCK ||
            originAccount.getAccountStatement() == AccountStatement.CANCELLED) {
            throw new BusinessException("La cuenta origen está bloqueada o cancelada");
        }
        if (originAccount.getCurrentBalance().compareTo(transfer.getMount()) < 0) {
            throw new BusinessException("Saldo insuficiente en la cuenta origen para ejecutar la transferencia");
        }

        // Ejecutar la transferencia: actualizar saldos
        BankAccount destinationAccount = accountPort.findByAccountNumber(
                transfer.getDestinationAccount().getAccountNumber());
        if (destinationAccount == null) {
            throw new BusinessException("La cuenta destino no existe");
        }

        originAccount.setCurrentBalance(originAccount.getCurrentBalance().subtract(transfer.getMount()));
        destinationAccount.setCurrentBalance(destinationAccount.getCurrentBalance().add(transfer.getMount()));
        accountPort.save(originAccount);
        accountPort.save(destinationAccount);

        transfer.setTransferStatus(TransferStatus.EXECUTED);
        transfer.setApprovedUserId(approverUserId.intValue());
        transfer.setApprovalDate(new Date(System.currentTimeMillis()));
        transferPort.updateStatus(transferId, TransferStatus.EXECUTED);

        // Bitácora
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Transferencia_Aprobada_Ejecutada");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(approverUserId);
        binnacle.setRoleUser(approverRole);
        binnacle.setAffectedProductId(String.valueOf(transferId));
        binnaclePort.save(binnacle);
    }
}
