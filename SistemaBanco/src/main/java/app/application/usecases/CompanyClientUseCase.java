package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.Transfer;
import app.domain.models.User;
import app.domain.services.CreateCredit;
import app.domain.services.DelegatePermissions;
import app.domain.services.ApproveTransfer;
import app.domain.services.RejectTransfer;
import app.domain.services.SearchAccount;
import app.domain.services.SearchCredit;
import app.domain.services.SearchTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyClientUseCase {

    private final SearchAccount searchAccount;
    private final SearchCredit searchCredit;
    private final CreateCredit createCredit;
    private final DelegatePermissions delegatePermissions;
    private final ApproveTransfer approveTransfer;
    private final RejectTransfer rejectTransfer;
    private final SearchTransfer searchTransfer;

    @Autowired
    public CompanyClientUseCase(SearchAccount searchAccount, SearchCredit searchCredit,
                                 CreateCredit createCredit, DelegatePermissions delegatePermissions,
                                 ApproveTransfer approveTransfer, RejectTransfer rejectTransfer,
                                 SearchTransfer searchTransfer) {
        this.searchAccount = searchAccount;
        this.searchCredit = searchCredit;
        this.createCredit = createCredit;
        this.delegatePermissions = delegatePermissions;
        this.approveTransfer = approveTransfer;
        this.rejectTransfer = rejectTransfer;
        this.searchTransfer = searchTransfer;
    }

    public List<BankAccount> searchMyAccounts(Long document) throws BusinessException {
        return searchAccount.findByCustomerDocument(document);
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public List<Credit> searchMyCredits(Long document) throws BusinessException {
        return searchCredit.findByCustomerDocument(document);
    }

    public Credit searchCredit(Long creditId) throws BusinessException {
        return searchCredit.findById(creditId);
    }

    public void createCredit(Credit credit, Long userId) throws BusinessException {
        createCredit.createCredit(credit, userId, null);
    }

    public void delegatePermission(Long companyDocument, Long targetUserId, boolean grant) throws BusinessException {
        delegatePermissions.execute(companyDocument, targetUserId, grant);
    }

    public List<User> getCompanyUsers(Long companyDocument) throws BusinessException {
        return delegatePermissions.findUsersByCompany(companyDocument);
    }

    public void approveTransfer(Long transferId, Long userId) throws BusinessException {
        approveTransfer.approveTransfer(transferId, userId, null);
    }

    public void rejectTransfer(Long transferId, Long userId) throws BusinessException {
        rejectTransfer.rejectTransfer(transferId, userId, null);
    }

    public Transfer searchTransfer(Long transferId) throws BusinessException {
        return searchTransfer.findById(transferId);
    }

    public List<Transfer> searchMyTransfers(String accountNumber) throws BusinessException {
        return searchTransfer.findByAccountNumber(accountNumber);
    }
}