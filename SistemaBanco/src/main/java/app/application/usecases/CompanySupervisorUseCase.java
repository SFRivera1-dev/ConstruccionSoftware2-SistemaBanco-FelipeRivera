package app.application.usecases;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.models.Role;
import app.domain.models.Transfer;
import app.domain.models.User;
import app.domain.services.ApproveTransfer;
import app.domain.services.ManageCompanyUser;
import app.domain.services.RejectTransfer;
import app.domain.services.SearchAccount;
import app.domain.services.SearchTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class CompanySupervisorUseCase {

    private final SearchAccount searchAccount;
    private final SearchTransfer searchTransfer;
    private final ApproveTransfer approveTransfer;
    private final RejectTransfer rejectTransfer;
    private final ManageCompanyUser manageCompanyUser;

    @Autowired
    public CompanySupervisorUseCase(SearchAccount searchAccount, SearchTransfer searchTransfer,
                                     ApproveTransfer approveTransfer, RejectTransfer rejectTransfer,
                                     ManageCompanyUser manageCompanyUser) {
        this.searchAccount = searchAccount;
        this.searchTransfer = searchTransfer;
        this.approveTransfer = approveTransfer;
        this.rejectTransfer = rejectTransfer;
        this.manageCompanyUser = manageCompanyUser;
    }

    public BankAccount searchAccount(String accountNumber) throws BusinessException {
        return searchAccount.findByAccountNumber(accountNumber);
    }

    public Transfer searchTransfer(Long transferId) throws BusinessException {
        return searchTransfer.findById(transferId);
    }

    public List<BankAccount> searchAccountsByCompany(Long document) throws BusinessException {
    return searchAccount.findByCustomerDocument(document);
}

    public List<Transfer> searchPendingTransfers() {
        return searchTransfer.findPendingTransfers();
    }

    public void approveTransfer(Long transferId, Long userId) throws BusinessException {
        approveTransfer.approveTransfer(transferId, userId, Role.COMPANY_SUPERVISOR);
    }

    public void rejectTransfer(Long transferId, Long userId) throws BusinessException {
        rejectTransfer.rejectTransfer(transferId, userId, Role.COMPANY_SUPERVISOR);
    }

    public List<User> getCompanyUsers(Long companyDocument) throws BusinessException {
        return manageCompanyUser.getCompanyUsers(companyDocument);
    }

    public void manageUser(Long companyDocument, Long targetUserId, boolean enable) throws BusinessException {
        manageCompanyUser.manageUser(companyDocument, targetUserId, enable);
    }

    public List<Transfer> searchPendingTransfersByCompany(Long document) throws BusinessException {
        List<String> accountNumbers = searchAccount.findByCustomerDocument(document)
                .stream()
                .map(BankAccount::getAccountNumber)
                .collect(java.util.stream.Collectors.toList());
        return searchTransfer.findPendingTransfersByAccounts(accountNumbers);
    }
}