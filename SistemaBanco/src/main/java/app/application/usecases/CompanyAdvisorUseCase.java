package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.services.CreateBatchPayment;
import app.domain.services.CreateTransfer;
import app.domain.services.SearchAccount;
import app.domain.services.SearchClient;
import app.domain.services.SearchTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.domain.models.User;
import java.util.List;

@Service
public class CompanyAdvisorUseCase {

    private final SearchAccount searchAccount;
    private final CreateTransfer createTransfer;
    private final CreateBatchPayment createBatchPaymet;
    private final SearchTransfer searchTransfer;
    private final SearchClient searchClient;

    @Autowired
    public CompanyAdvisorUseCase(SearchAccount searchAccount, CreateTransfer createTransfer,
                                  CreateBatchPayment createBatchPaymet, SearchTransfer searchTransfer,
                                SearchClient searchClient) {
        this.searchAccount = searchAccount;
        this.createTransfer = createTransfer;
        this.createBatchPaymet = createBatchPaymet;
        this.searchTransfer = searchTransfer;
        this.searchClient = searchClient;
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public User findUserByDocument(Long document) throws BusinessException {
    return searchClient.findUserByDocument(document);
}

    public void createTransfer(Transfer transfer, Long userId) throws BusinessException {
        createTransfer.createTransfer(transfer, userId, Role.COMPANY_ADVISOR);
    }

    public void createBatchPayment(List<Transfer> transfers, Long userId) throws BusinessException {
        createBatchPaymet.createBatchPayment(transfers, userId, Role.COMPANY_ADVISOR);
    }

    public List<BankAccount> searchAccountsByCompany(Long document) throws BusinessException {
    return searchAccount.findByCustomerDocument(document);
}

    public Transfer searchTransfer(Long transferId) throws BusinessException {
        return searchTransfer.findById(transferId);
    }

    public List<Transfer> searchTransfersByAccount(String accountNumber) throws BusinessException {
        return searchTransfer.findByAccountNumber(accountNumber);
    }
}