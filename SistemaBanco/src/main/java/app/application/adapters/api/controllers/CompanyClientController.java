package app.application.adapters.api.controllers;

import app.application.adapters.api.request.CreditRequest;
import app.application.adapters.api.request.TransferRequest;
import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.CreditResponse;
import app.application.adapters.api.response.TransferResponse;
import app.application.usecases.CompanyClientUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.CustomerCompany;
import app.domain.models.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company-client")
public class CompanyClientController {

    private final CompanyClientUseCase companyClientUseCase;

    public CompanyClientController(CompanyClientUseCase companyClientUseCase) {
        this.companyClientUseCase = companyClientUseCase;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> searchMyAccounts(Authentication authentication) {
        String document = (String) authentication.getDetails();
        List<AccountResponse> accounts = companyClientUseCase
                .searchMyAccounts(Long.parseLong(document))
                .stream().map(CompanyClientController::toAccountResponse).toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(@PathVariable String accountNumber) {
        BankAccount account = companyClientUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @GetMapping("/credits")
    public ResponseEntity<List<CreditResponse>> searchMyCredits(Authentication authentication) {
        String document = (String) authentication.getDetails();
        List<CreditResponse> credits = companyClientUseCase
                .searchMyCredits(Long.parseLong(document))
                .stream().map(CompanyClientController::toCreditResponse).toList();
        return ResponseEntity.ok(credits);
    }

    @GetMapping("/credits/{creditId}")
    public ResponseEntity<CreditResponse> searchCredit(@PathVariable Long creditId) {
        Credit credit = companyClientUseCase.searchCredit(creditId);
        return ResponseEntity.ok(toCreditResponse(credit));
    }

    @PostMapping("/credits")
    public ResponseEntity<CreditResponse> createCredit(
            @Valid @RequestBody CreditRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Credit credit = toCredit(request);
        companyClientUseCase.createCredit(credit, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toCreditResponse(credit));
    }

    @PutMapping("/permissions/{targetUserId}")
    public ResponseEntity<Void> delegatePermission(
            @PathVariable Long targetUserId,
            @RequestParam boolean grant,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        companyClientUseCase.delegatePermission(Long.parseLong(document), targetUserId, grant);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getCompanyUsers(Authentication authentication) {
        String document = (String) authentication.getDetails();
        return ResponseEntity.ok(
                companyClientUseCase.getCompanyUsers(Long.parseLong(document)));
    }

    @PutMapping("/transfers/{transferId}/approve")
    public ResponseEntity<Void> approveTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        companyClientUseCase.approveTransfer(transferId, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfers/{transferId}/reject")
    public ResponseEntity<Void> rejectTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        companyClientUseCase.rejectTransfer(transferId, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<TransferResponse> searchTransfer(@PathVariable Long transferId) {
        Transfer transfer = companyClientUseCase.searchTransfer(transferId);
        return ResponseEntity.ok(toTransferResponse(transfer));
    }

    @GetMapping("/transfers/account/{accountNumber}")
    public ResponseEntity<List<TransferResponse>> searchMyTransfers(
            @PathVariable String accountNumber) {
        List<TransferResponse> transfers = companyClientUseCase
                .searchMyTransfers(accountNumber)
                .stream().map(CompanyClientController::toTransferResponse).toList();
        return ResponseEntity.ok(transfers);
    }

    private static AccountResponse toAccountResponse(BankAccount account) {
        String holderName = account.getAccountHolderID() != null
                ? account.getAccountHolderID().getName() : null;
        Long holderDocument = account.getAccountHolderID() != null
                ? account.getAccountHolderID().getDocument() : null;
        return new AccountResponse(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrentBalance(),
                account.getCurrency(),
                account.getAccountStatement(),
                account.getOpeningDate(),
                holderName,
                holderDocument
        );
    }

    private static Credit toCredit(CreditRequest request) {
        Credit credit = new Credit();
        credit.setCreditType(request.getCreditType());
        credit.setAmountRequested(request.getAmountRequested());
        credit.setInterestRate(request.getInterestRate());
        credit.setTermMonths(request.getTermMonths());
        CustomerCompany company = new CustomerCompany();
        company.setDocument(request.getCustomerDocument());
        credit.setCustomerRequestId(company);
        return credit;
    }

    private static CreditResponse toCreditResponse(Credit credit) {
        Long customerDocument = credit.getCustomerRequestId() != null
                ? credit.getCustomerRequestId().getDocument() : null;
        String customerName = credit.getCustomerRequestId() != null
                ? credit.getCustomerRequestId().getName() : null;
        return new CreditResponse(
                credit.getIdCredit(),
                credit.getCreditType(),
                credit.getAmountRequested(),
                credit.getAmountApproved(),
                credit.getInterestRate(),
                credit.getTermMonths(),
                credit.getCreditStatus(),
                credit.getApprovalDate(),
                credit.getDisbursementDate(),
                credit.getDestinationAccount(),
                customerDocument,
                customerName
        );
    }

    private static Transfer toTransfer(TransferRequest request) {
        Transfer transfer = new Transfer();
        BankAccount origin = new BankAccount();
        origin.setAccountNumber(request.getOriginAccount());
        transfer.setOriginAccount(origin);
        BankAccount destination = new BankAccount();
        destination.setAccountNumber(request.getDestinationAccount());
        transfer.setDestinationAccount(destination);
        transfer.setMount(request.getMount());
        return transfer;
    }

    private static TransferResponse toTransferResponse(Transfer transfer) {
        String originAccount = transfer.getOriginAccount() != null
                ? transfer.getOriginAccount().getAccountNumber() : null;
        String destinationAccount = transfer.getDestinationAccount() != null
                ? transfer.getDestinationAccount().getAccountNumber() : null;
        return new TransferResponse(
                transfer.getIdTransfer(),
                originAccount,
                destinationAccount,
                transfer.getMount(),
                transfer.getCreationDate(),
                transfer.getApprovalDate(),
                transfer.getTransferStatus(),
                transfer.getCreatorUserId(),
                transfer.getApprovedUserId()
        );
    }
}