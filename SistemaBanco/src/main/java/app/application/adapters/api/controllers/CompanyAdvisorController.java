package app.application.adapters.api.controllers;

import app.application.adapters.api.request.TransferRequest;
import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.TransferResponse;
import app.application.usecases.CompanyAdvisorUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import app.domain.models.User;
import java.util.List;

@RestController
@RequestMapping("/company-advisor")
public class CompanyAdvisorController {

    private final CompanyAdvisorUseCase companyAdvisorUseCase;

    public CompanyAdvisorController(CompanyAdvisorUseCase companyAdvisorUseCase) {
        this.companyAdvisorUseCase = companyAdvisorUseCase;
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {
        String document = (String) authentication.getDetails();

        User user = companyAdvisorUseCase.findUserByDocument(Long.parseLong(document));
        Long companyDocument = user.getIdCustomer();

        List<BankAccount> myAccounts = companyAdvisorUseCase
                .searchAccountsByCompany(companyDocument);

        boolean isOwner = myAccounts.stream()
                .anyMatch(acc -> acc.getAccountNumber().equals(accountNumber));

        if (!isOwner) {
            throw new app.domain.Exceptions.BusinessException(
                    "No tienes permisos para ver esta cuenta");
        }

        BankAccount account = companyAdvisorUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Transfer transfer = toTransfer(request);
        companyAdvisorUseCase.createTransfer(transfer, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toTransferResponse(transfer));
    }

    @PostMapping("/batch-payments")
    public ResponseEntity<Void> createBatchPayment(
            @Valid @RequestBody List<TransferRequest> requests,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        List<Transfer> transfers = requests.stream()
                .map(CompanyAdvisorController::toTransfer).toList();
        companyAdvisorUseCase.createBatchPayment(transfers, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<TransferResponse> searchTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Transfer transfer = companyAdvisorUseCase.searchTransfer(transferId);

        User user = companyAdvisorUseCase.findUserByDocument(Long.parseLong(document));
        List<BankAccount> myAccounts = companyAdvisorUseCase
                .searchAccountsByCompany(user.getIdCustomer());

        String originAccount = transfer.getOriginAccount() != null
                ? transfer.getOriginAccount().getAccountNumber() : "";
        String destinationAccount = transfer.getDestinationAccount() != null
                ? transfer.getDestinationAccount().getAccountNumber() : "";

        boolean isOwner = myAccounts.stream()
                .anyMatch(acc -> acc.getAccountNumber().equals(originAccount)
                        || acc.getAccountNumber().equals(destinationAccount));

        if (!isOwner) {
            throw new app.domain.Exceptions.BusinessException(
                    "No tienes permisos para ver esta transferencia");
        }

        return ResponseEntity.ok(toTransferResponse(transfer));
    }

    @GetMapping("/transfers/account/{accountNumber}")
    public ResponseEntity<List<TransferResponse>> searchTransfersByAccount(
            @PathVariable String accountNumber) {
        List<TransferResponse> transfers = companyAdvisorUseCase
                .searchTransfersByAccount(accountNumber)
                .stream().map(CompanyAdvisorController::toTransferResponse).toList();
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