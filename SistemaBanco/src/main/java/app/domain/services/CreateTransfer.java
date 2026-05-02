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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateTransfer {

    private static final BigDecimal APPROVAL_THRESHOLD = new BigDecimal("100000000");

    private final TransferPort transferPort;
    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    public CreateTransfer(TransferPort transferPort, AccountPort accountPort, BinnaclePort binnaclePort) {
        this.transferPort = transferPort;
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void createTransfer(Transfer transfer, Long creatorUserId, Role creatorRole) throws BusinessException {
        if (transfer.getMount() == null || transfer.getMount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto de la transferencia debe ser mayor que cero");
        }

        BankAccount originAccount = accountPort.findByAccountNumber(
                transfer.getOriginAccount().getAccountNumber());
        if (originAccount == null) {
            throw new BusinessException("La cuenta origen no existe");
        }

        if (originAccount.getAccountStatement() == AccountStatement.BLOCK ||
            originAccount.getAccountStatement() == AccountStatement.CANCELLED) {
            throw new BusinessException("No se pueden realizar transferencias desde cuentas bloqueadas o canceladas");
        }

        BankAccount destinationAccount = accountPort.findByAccountNumber(
                transfer.getDestinationAccount().getAccountNumber());
        if (destinationAccount == null) {
            throw new BusinessException("La cuenta destino no existe");
        }

        transfer.setCreationDate(new Date(System.currentTimeMillis()));
        transfer.setCreatorUserId(creatorUserId);

        if (transfer.getMount().compareTo(APPROVAL_THRESHOLD) > 0) {
            transfer.setTransferStatus(TransferStatus.AWAITING_APPROVAL);
            transferPort.save(transfer);

            Details details = new Details();
            details.setMount(transfer.getMount());
            details.setNewStatus(TransferStatus.AWAITING_APPROVAL.name());
            details.setAccountNumber(transfer.getOriginAccount().getAccountNumber());

            Binnacle binnacle = new Binnacle();
            binnacle.setOperationType("Transferencia_En_Espera");
            binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
            binnacle.setIdUser(creatorUserId);
            binnacle.setRoleUser(creatorRole);
            binnacle.setAffectedProductId(String.valueOf(transfer.getIdTransfer()));
            binnacle.setDetails(details);
            binnaclePort.save(binnacle);

        } else {
            if (originAccount.getCurrentBalance().compareTo(transfer.getMount()) < 0) {
                throw new BusinessException("Saldo insuficiente en la cuenta origen");
            }

            BigDecimal balanceBeforeOrigin = originAccount.getCurrentBalance();
            BigDecimal balanceBeforeDestination = destinationAccount.getCurrentBalance();

            originAccount.setCurrentBalance(originAccount.getCurrentBalance().subtract(transfer.getMount()));
            destinationAccount.setCurrentBalance(destinationAccount.getCurrentBalance().add(transfer.getMount()));
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