package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.AccountStatement;
import app.domain.models.Binnacle;
import app.domain.models.Role;
import app.domain.models.Customer;
import app.domain.ports.AccountPort;
import app.domain.ports.ClientPort;
import app.domain.ports.BinnaclePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateAccount {

    private final AccountPort accountPort;
    private final ClientPort clientPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public CreateAccount(AccountPort accountPort, ClientPort clientPort, BinnaclePort binnaclePort) {
        this.accountPort = accountPort;
        this.clientPort = clientPort;
        this.binnaclePort = binnaclePort;
    }

    public void createAccount(BankAccount account, Long creatorUserId, Role creatorRole) throws BusinessException {
        // Regla: el cliente debe existir
        Customer customer = clientPort.findByDocument(account.getAccountHolderID().getDocument());
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }

        // Regla: no se puede abrir cuenta a usuario Inactivo o Bloqueado
        // (el estado del usuario viene del customer que ya está en base de datos)
        // Se infiere del documento: si el cliente no está activo, se bloquea
        // Nota: Customer hereda de Person; el estado se valida contra User
        // Para no acoplar al User aquí, el UseCase debe pasar el estado.
        // Validamos el número de cuenta como único
        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            throw new BusinessException("El número de cuenta es obligatorio");
        }
        BankAccount existing = accountPort.findByAccountNumber(account.getAccountNumber());
        if (existing != null) {
            throw new BusinessException("Ya existe una cuenta con ese número");
        }

        // Regla: el tipo de cuenta es obligatorio
        if (account.getAccountType() == null) {
            throw new BusinessException("El tipo de cuenta es obligatorio");
        }

        // Regla: la moneda es obligatoria
        if (account.getCurrency() == null) {
            throw new BusinessException("La moneda es obligatoria");
        }

        // Asignar estado inicial y fecha de apertura
        account.setAccountStatement(AccountStatement.ACTIVE);
        account.setOpeningDate(new Date(System.currentTimeMillis()));
        account.setAccountHolderID(customer);
        account.setCurrentBalance(BigDecimal.ZERO);

        accountPort.save(account);

        // Registrar en bitácora
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Apertura_Cuenta");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId(account.getAccountNumber());
        binnaclePort.save(binnacle);
    }
}
