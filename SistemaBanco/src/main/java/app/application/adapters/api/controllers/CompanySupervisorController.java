package app.application.adapters.api.controllers;

import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.TransferResponse;
import app.application.usecases.CompanySupervisorUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Transfer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company-supervisor")
public class CompanySupervisorController {

    private final CompanySupervisorUseCase companySupervisorUseCase;

    public CompanySupervisorController(CompanySupervisorUseCase companySupervisorUseCase) {
        this.companySupervisorUseCase = companySupervisorUseCase;
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {
        String document = (String) authentication.getDetails();

        List<BankAccount> myAccounts = companySupervisorUseCase
                .searchAccountsByCompany(Long.parseLong(document));

        boolean isOwner = myAccounts.stream()
                .anyMatch(acc -> acc.getAccountNumber().equals(accountNumber));

        if (!isOwner) {
            throw new app.domain.Exceptions.BusinessException(
                    "No tienes permisos para ver esta cuenta");
        }

        BankAccount account = companySupervisorUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<TransferResponse> searchTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Transfer transfer = companySupervisorUseCase.searchTransfer(transferId);

        List<BankAccount> myAccounts = companySupervisorUseCase
                .searchAccountsByCompany(Long.parseLong(document));

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

    @GetMapping("/transfers/pending")
    public ResponseEntity<List<TransferResponse>> searchPendingTransfers() {
        List<TransferResponse> transfers = companySupervisorUseCase.searchPendingTransfers()
                .stream().map(CompanySupervisorController::toTransferResponse).toList();
        return ResponseEntity.ok(transfers);
    }

    @PutMapping("/transfers/{transferId}/approve")
    public ResponseEntity<Void> approveTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        companySupervisorUseCase.approveTransfer(transferId, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/transfers/{transferId}/reject")
    public ResponseEntity<Void> rejectTransfer(
            @PathVariable Long transferId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        companySupervisorUseCase.rejectTransfer(transferId, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/company/{companyDocument}/users")
    public ResponseEntity<?> getCompanyUsers(@PathVariable Long companyDocument) {
        return ResponseEntity.ok(companySupervisorUseCase.getCompanyUsers(companyDocument));
    }

    @PutMapping("/company/{companyDocument}/users/{userId}")
    public ResponseEntity<Void> manageUser(
            @PathVariable Long companyDocument,
            @PathVariable Long userId,
            @RequestParam boolean enable) {
        companySupervisorUseCase.manageUser(companyDocument, userId, enable);
        return ResponseEntity.ok().build();
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