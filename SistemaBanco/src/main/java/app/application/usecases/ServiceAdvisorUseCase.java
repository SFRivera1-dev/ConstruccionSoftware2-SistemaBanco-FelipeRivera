package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.services.CreateAccount;
import app.domain.services.Deposit;
import app.domain.services.SearchAccount;
import app.domain.services.Withdraw;
import app.domain.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ServiceAdvisorUseCase {

    private final SearchAccount searchAccount;
    private final CreateAccount createAccount;
    private final Deposit deposit;
    private final Withdraw withdraw;

    @Autowired
    public ServiceAdvisorUseCase(SearchAccount searchAccount, CreateAccount createAccount,
                                  Deposit deposit, Withdraw withdraw) {
        this.searchAccount = searchAccount;
        this.createAccount = createAccount;
        this.deposit = deposit;
        this.withdraw = withdraw;
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public List<BankAccount> searchAccountsByCustomer(Long document) throws BusinessException {
        return searchAccount.findByCustomerDocument(document);
    }

    public void createAccount(BankAccount account, Long userId) throws BusinessException {
        createAccount.createAccount(account, userId, Role.SERVICE_ADVISOR);
    }

    public void deposit(String accountNumber, BigDecimal amount, Long userId) throws BusinessException {
        deposit.deposit(accountNumber, amount, userId, Role.SERVICE_ADVISOR);
    }

    public void withdraw(String accountNumber, BigDecimal amount, Long userId) throws BusinessException {
        withdraw.withdraw(accountNumber, amount, userId, Role.SERVICE_ADVISOR);
    }
}