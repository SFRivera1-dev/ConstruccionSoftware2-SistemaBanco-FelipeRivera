package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.AccountStatement;
import app.domain.models.Binnacle;
import app.domain.models.Role;
import app.domain.ports.AccountPort;
import app.domain.ports.BinnaclePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class Deposit {

    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public Deposit(AccountPort accountPort, BinnaclePort binnaclePort) {
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void deposit(String accountNumber, BigDecimal amount, Long creatorUserId, Role creatorRole) throws BusinessException {
        // Regla: la cuenta debe existir
        BankAccount account = accountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("No existe una cuenta con ese número");
        }

        // Regla: no se permiten operaciones en cuentas Bloqueadas o Canceladas
        if (account.getAccountStatement() == AccountStatement.BLOCK ||
            account.getAccountStatement() == AccountStatement.CANCELLED) {
            throw new BusinessException("No se pueden realizar operaciones en cuentas bloqueadas o canceladas");
        }

        // Regla: el monto debe ser mayor que cero
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del depósito debe ser mayor que cero");
        }

        // Ejecutar depósito
        account.setCurrentBalance(account.getCurrentBalance().add(amount));
        accountPort.save(account);

        // Registrar en bitácora
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Deposito");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId(accountNumber);
        binnaclePort.save(binnacle);
    }
}
