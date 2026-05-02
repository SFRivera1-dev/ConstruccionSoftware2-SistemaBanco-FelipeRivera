package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatement;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Customer;
import app.domain.models.Details;
import app.domain.models.Role;
import app.domain.ports.AccountPort;
import app.domain.ports.BinnaclePort;
import app.domain.ports.ClientPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateAccount {

    private final AccountPort accountPort;
    private final ClientPort clientPort;
    private final BinnaclePort binnaclePort;

    public CreateAccount(AccountPort accountPort, ClientPort clientPort, BinnaclePort binnaclePort) {
        this.accountPort = accountPort;
        this.clientPort = clientPort;
        this.binnaclePort = binnaclePort;
    }

    public void createAccount(BankAccount account, Long creatorUserId, Role creatorRole) throws BusinessException {
        Customer customer = clientPort.findByDocument(account.getAccountHolderID().getDocument());
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }

        if (account.getAccountNumber() == null || account.getAccountNumber().isBlank()) {
            throw new BusinessException("El número de cuenta es obligatorio");
        }

        BankAccount existing = accountPort.findByAccountNumber(account.getAccountNumber());
        if (existing != null) {
            throw new BusinessException("Ya existe una cuenta con ese número");
        }

        if (account.getAccountType() == null) {
            throw new BusinessException("El tipo de cuenta es obligatorio");
        }

        if (account.getCurrency() == null) {
            throw new BusinessException("La moneda es obligatoria");
        }

        account.setAccountStatement(AccountStatement.ACTIVE);
        account.setOpeningDate(new Date(System.currentTimeMillis()));
        account.setAccountHolderID(customer);
        account.setCurrentBalance(BigDecimal.ZERO);

        accountPort.save(account);

        Details details = new Details();
        details.setAccountNumber(account.getAccountNumber());
        details.setAccountType(account.getAccountType().name());
        details.setNewStatus(AccountStatement.ACTIVE.name());

        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Apertura_Cuenta");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId(account.getAccountNumber());
        binnacle.setDetails(details);
        binnaclePort.save(binnacle);
    }
}