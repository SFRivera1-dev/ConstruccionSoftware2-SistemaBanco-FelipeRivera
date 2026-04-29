package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatement;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.AccountPort;
import app.domain.ports.BatchPaymentPort;
import app.domain.ports.BinnaclePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
public class CreateBatchPayment {

    // Mismo umbral que CreateTransfer: transferencias que superen esto requieren aprobación
    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("100000000");

    private final BatchPaymentPort batchPaymentPort;
    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public CreateBatchPayment(BatchPaymentPort batchPaymentPort, AccountPort accountPort,
                              BinnaclePort binnaclePort) {
        this.batchPaymentPort = batchPaymentPort;
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void createBatchPayment(List<Transfer> transfers, Long creatorUserId, Role creatorRole) throws BusinessException {
        // Regla: el lote debe tener al menos una transferencia
        if (transfers == null || transfers.isEmpty()) {
            throw new BusinessException("El lote de pagos debe contener al menos una transferencia");
        }

        for (Transfer transfer : transfers) {
            // Regla: cada monto debe ser mayor que cero
            if (transfer.getMount() == null || transfer.getMount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Todos los montos del lote deben ser mayores que cero");
            }

            // Regla: la cuenta origen debe existir y estar activa
            BankAccount originAccount = accountPort.findByAccountNumber(
                    transfer.getOriginAccount().getAccountNumber());
            if (originAccount == null) {
                throw new BusinessException("La cuenta origen '" +
                        transfer.getOriginAccount().getAccountNumber() + "' no existe");
            }
            if (originAccount.getAccountStatement() == AccountStatement.BLOCK ||
                originAccount.getAccountStatement() == AccountStatement.CANCELLED) {
                throw new BusinessException("La cuenta origen '" +
                        transfer.getOriginAccount().getAccountNumber() +
                        "' está bloqueada o cancelada");
            }

            // Regla: la cuenta destino debe existir
            BankAccount destinationAccount = accountPort.findByAccountNumber(
                    transfer.getDestinationAccount().getAccountNumber());
            if (destinationAccount == null) {
                throw new BusinessException("La cuenta destino '" +
                        transfer.getDestinationAccount().getAccountNumber() + "' no existe");
            }

            transfer.setCreationDate(new Date(System.currentTimeMillis()));
            transfer.setCreatorUserId(creatorUserId);

            // Regla: si el monto supera el umbral → espera aprobación del supervisor
            if (transfer.getMount().compareTo(APPROVAL_THRESHOLD) > 0) {
                transfer.setTransferStatus(TransferStatus.AWAITING_APPROVAL);
            } else {
                // Regla: saldo suficiente para ejecutar directamente
                if (originAccount.getCurrentBalance().compareTo(transfer.getMount()) < 0) {
                    throw new BusinessException("Saldo insuficiente en la cuenta origen '"
                            + transfer.getOriginAccount().getAccountNumber() + "'");
                }
                originAccount.setCurrentBalance(originAccount.getCurrentBalance().subtract(transfer.getMount()));
                destinationAccount.setCurrentBalance(destinationAccount.getCurrentBalance().add(transfer.getMount()));
                accountPort.save(originAccount);
                accountPort.save(destinationAccount);
                transfer.setTransferStatus(TransferStatus.EXECUTED);
            }
        }

        // Guardar todo el lote
        batchPaymentPort.saveAll(transfers);

        // Bitácora: un registro por lote completo
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Pago_Masivo");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId("BATCH-" + creatorUserId + "-" + System.currentTimeMillis());
        binnaclePort.save(binnacle);
    }
}
