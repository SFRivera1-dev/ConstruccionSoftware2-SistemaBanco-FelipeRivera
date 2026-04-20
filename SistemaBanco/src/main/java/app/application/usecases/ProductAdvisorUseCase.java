package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.Customer;
import app.domain.models.Role;
import app.domain.services.CreateAccount;
import app.domain.services.CreateCredit;
import app.domain.services.SearchAccount;
import app.domain.services.SearchClient;
import app.domain.services.SearchCreditStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProductAdvisorUseCase {

    private final SearchAccount searchAccount;
    private final SearchClient searchClient;
    private final CreateAccount createAccount;
    private final CreateCredit createCredit;
    private final SearchCreditStatus searchCreditStatus;

    @Autowired
    public ProductAdvisorUseCase(SearchAccount searchAccount, SearchClient searchClient,
                                  CreateAccount createAccount, CreateCredit createCredit,
                                  SearchCreditStatus searchCreditStatus) {
        this.searchAccount = searchAccount;
        this.searchClient = searchClient;
        this.createAccount = createAccount;
        this.createCredit = createCredit;
        this.searchCreditStatus = searchCreditStatus;
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public Customer searchClient(Long document) throws BusinessException {
        return searchClient.findByDocument(document);
    }

    public void createAccount(BankAccount account, Long userId) throws BusinessException {
        createAccount.createAccount(account, userId, Role.PRODUCT_ADVISOR);
    }

    public void createCredit(Credit credit, Long userId) throws BusinessException {
        createCredit.createCredit(credit, userId, Role.PRODUCT_ADVISOR);
    }

    public Credit searchCreditStatus(Long creditId) throws BusinessException {
        return searchCreditStatus.findCreditStatus(creditId);
    }
}