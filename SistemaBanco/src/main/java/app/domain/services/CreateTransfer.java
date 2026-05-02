package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatement;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Details;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.models.TransferStatus;
import app.domain.ports.AccountPort;
import app.domain.ports.BinnaclePort;
import app.domain.ports.TransferPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateTransfer {

    // Umbral de monto que requiere aprobación de supervisor (100 millones COP por ejemplo)
    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("100000000");

    private final TransferPort transferPort;
    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public CreateTransfer(TransferPort transferPort, AccountPort accountPort, BinnaclePort binnaclePort) {
        this.transferPort = transferPort;
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void createTransfer(Transfer transfer, Long creatorUserId, Role creatorRole) throws BusinessException {
        // Regla: el monto debe ser mayor que cero
        if (transfer.getMount() == null || transfer.getMount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto de la transferencia debe ser mayor que cero");
        }

        // Regla: la cuenta origen debe existir
        BankAccount originAccount = accountPort.findByAccountNumber(
                transfer.getOriginAccount().getAccountNumber());
        if (originAccount == null) {
            throw new BusinessException("La cuenta origen no existe");
        }

        // Regla: la cuenta origen no puede estar Bloqueada o Cancelada
        if (originAccount.getAccountStatement() == AccountStatement.BLOCK ||
            originAccount.getAccountStatement() == AccountStatement.CANCELLED) {
            throw new BusinessException("No se pueden realizar transferencias desde cuentas bloqueadas o canceladas");
        }

        // Regla: la cuenta destino debe existir
        BankAccount destinationAccount = accountPort.findByAccountNumber(
                transfer.getDestinationAccount().getAccountNumber());
        if (destinationAccount == null) {
            throw new BusinessException("La cuenta destino no existe");
        }

        transfer.setCreationDate(new Date(System.currentTimeMillis()));
        transfer.setCreatorUserId(creatorUserId);

        // Regla: si el monto supera el umbral → queda en espera de aprobación
        // Si no supera el umbral → se ejecuta directamente (aplica a persona natural también)
        if (transfer.getMount().compareTo(APPROVAL_THRESHOLD) > 0) {
            // Transferencia de alto monto: requiere aprobación del supervisor
            transfer.setTransferStatus(TransferStatus.AWAITING_APPROVAL);
            transferPort.save(transfer);

            Binnacle binnacle = new Binnacle();
            binnacle.setOperationType("Transferencia_En_Espera");
            binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
            binnacle.setIdUser(creatorUserId);
            binnacle.setRoleUser(creatorRole);
            binnacle.setAffectedProductId(String.valueOf(transfer.getIdTransfer()));
            binnaclePort.save(binnacle);
        } else {
            // Transferencia de bajo monto: validar saldo y ejecutar directamente
            if (originAccount.getCurrentBalance().compareTo(transfer.getMount()) < 0) {
                throw new BusinessException("Saldo insuficiente en la cuenta origen");
            }

            //Ejecuta la transferencia
            originAccount.setCurrentBalance(originAccount.getCurrentBalance().subtract(transfer.getMount()));
            destinationAccount.setCurrentBalance(destinationAccount.getCurrentBalance().add(transfer.getMount()));
            
            //Guardar saldos antes para la bitacora
            BigDecimal balanceBeforeOrigin = originAccount.getCurrentBalance().add(transfer.getMount());
            BigDecimal balanceBeforeDestination = destinationAccount.getCurrentBalance().subtract(transfer.getMount());

            accountPort.save(originAccount);
            accountPort.save(destinationAccount);

            transfer.setTransferStatus(TransferStatus.EXECUTED);
            transferPort.save(transfer);

            Details details = new Details();
            details.setMount(transfer.getMount());
            details.setBalanceBeforeOrigin(balanceBeforeOrigin);
            details.setBalanceAfterOrigin(originAccount.getCurrentBalance());
            details.setBalanceBeforeDestination(balanceBeforeDestination);
            details.setBalanceAfterDestination(destinationAccount.getCurrentBalance());

            Binnacle binnacle = new Binnacle();
            binnacle.setOperationType("Transferencia_Ejecutada");
            binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
            binnacle.setIdUser(creatorUserId);
            binnacle.setRoleUser(creatorRole);
            binnacle.setAffectedProductId(String.valueOf(transfer.getIdTransfer()));
            binnacle.setDetails(details);
            binnaclePort.save(binnacle);
        }
    }
}
