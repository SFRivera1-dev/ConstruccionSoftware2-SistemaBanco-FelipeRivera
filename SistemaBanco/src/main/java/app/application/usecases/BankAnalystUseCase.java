package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import app.domain.models.Customer;
import app.domain.services.ApproveCredit;
import app.domain.services.RejectCredit;
import app.domain.services.SearchAccount;
import app.domain.services.SearchBinnacle;
import app.domain.services.SearchClient;
import app.domain.services.SearchCredit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankAnalystUseCase {

    private final SearchAccount searchAccount;
    private final SearchClient searchClient;
    private final SearchCredit searchCredit;
    private final ApproveCredit approveCredit;
    private final RejectCredit rejectCredit;
    private final SearchBinnacle searchBinnacle;

    @Autowired
    public BankAnalystUseCase(SearchAccount searchAccount, SearchClient searchClient,
                               SearchCredit searchCredit, ApproveCredit approveCredit,
                               RejectCredit rejectCredit, SearchBinnacle searchBinnacle) {
        this.searchAccount = searchAccount;
        this.searchClient = searchClient;
        this.searchCredit = searchCredit;
        this.approveCredit = approveCredit;
        this.rejectCredit = rejectCredit;
        this.searchBinnacle = searchBinnacle;
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public Customer searchClient(Long document) throws BusinessException {
        return searchClient.findByDocument(document);
    }

    public Credit searchCredit(Long creditId) throws BusinessException {
        return searchCredit.findById(creditId);
    }

    public List<Credit> searchCreditsByCustomer(Long document) throws BusinessException {
        return searchCredit.findByCustomerDocument(document);
    }

    public void approveCredit(Long creditId, BigDecimal amountApproved, Long userId) throws BusinessException {
        approveCredit.execute(creditId, amountApproved, null, userId, false);
    }

    public void disbursCredit(Long creditId, String destinationAccount, Long userId) throws BusinessException {
        approveCredit.execute(creditId, null, destinationAccount, userId, true);
    }

    public void rejectCredit(Long creditId, Long userId) throws BusinessException {
        rejectCredit.rejectCredit(creditId, userId);
    }

    public List<Binnacle> searchAllBinnacle() {
        return searchBinnacle.findAll();
    }

    public List<Binnacle> searchBinnacleByProduct(String productId) throws BusinessException {
        return searchBinnacle.findByProductId(productId);
    }
}