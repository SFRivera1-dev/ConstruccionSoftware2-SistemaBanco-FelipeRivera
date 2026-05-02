package app.application.usecases;
import app.domain.services.SearchBinnacle;
import app.domain.models.Binnacle;
import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.Transfer;
import app.domain.services.CreateCredit;
import app.domain.services.CreateTransfer;
import app.domain.services.SearchAccount;
import app.domain.services.SearchCredit;
import app.domain.services.SearchTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerPersonUseCase {

    private final SearchAccount searchAccount;
    private final CreateCredit createCredit;
    private final CreateTransfer createTransfer;
    private final SearchCredit searchCredit;
    private final SearchTransfer searchTransfer;
    private final SearchBinnacle searchBinnacle;

    @Autowired
    public CustomerPersonUseCase(SearchAccount searchAccount, CreateCredit createCredit,
                                  CreateTransfer createTransfer, SearchCredit searchCredit,
                                  SearchTransfer searchTransfer, SearchBinnacle searchBinnacle) {
        this.searchAccount = searchAccount;
        this.createCredit = createCredit;
        this.createTransfer = createTransfer;
        this.searchCredit = searchCredit;
        this.searchTransfer = searchTransfer;
        this.searchBinnacle = searchBinnacle;
    }

    public List<BankAccount> searchMyAccounts(Long document) throws BusinessException {
        return searchAccount.findByCustomerDocument(document);
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public void createCredit(Credit credit, Long userId) throws BusinessException {
        createCredit.createCredit(credit, userId, null);
    }

    public void createTransfer(Transfer transfer, Long userId) throws BusinessException {
        createTransfer.createTransfer(transfer, userId, null);
    }

    public List<Credit> searchMyCredits(Long document) throws BusinessException {
        return searchCredit.findByCustomerDocument(document);
    }

    public Credit searchCredit(Long creditId) throws BusinessException {
        return searchCredit.findById(creditId);
    }

    public List<Binnacle> searchMyBinnacle(String productId) throws BusinessException {
    return searchBinnacle.findByProductId(productId);
}

    public Transfer searchTransfer(Long transferId) throws BusinessException {
        return searchTransfer.findById(transferId);
    }

    public List<Transfer> searchMyTransfers(String accountNumber) throws BusinessException {
        return searchTransfer.findByAccountNumber(accountNumber);
    }
}