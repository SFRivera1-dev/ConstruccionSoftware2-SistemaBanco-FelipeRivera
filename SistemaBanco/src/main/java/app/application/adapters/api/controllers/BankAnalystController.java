package app.application.adapters.api.controllers;

import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.CreditResponse;
import app.application.usecases.BankAnalystUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/bank-analyst")
public class BankAnalystController {

    private final BankAnalystUseCase bankAnalystUseCase;

    public BankAnalystController(BankAnalystUseCase bankAnalystUseCase) {
        this.bankAnalystUseCase = bankAnalystUseCase;
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(@PathVariable String accountNumber) {
        BankAccount account = bankAnalystUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @GetMapping("/clients/{document}")
    public ResponseEntity<?> searchClient(@PathVariable Long document) {
        return ResponseEntity.ok(bankAnalystUseCase.searchClient(document));
    }

    @GetMapping("/credits/{creditId}")
    public ResponseEntity<CreditResponse> searchCredit(@PathVariable Long creditId) {
        Credit credit = bankAnalystUseCase.searchCredit(creditId);
        return ResponseEntity.ok(toCreditResponse(credit));
    }

    @GetMapping("/credits/customer/{document}")
    public ResponseEntity<List<CreditResponse>> searchCreditsByCustomer(
            @PathVariable Long document) {
        List<CreditResponse> credits = bankAnalystUseCase.searchCreditsByCustomer(document)
                .stream().map(BankAnalystController::toCreditResponse).toList();
        return ResponseEntity.ok(credits);
    }

    @PutMapping("/credits/{creditId}/approve")
    public ResponseEntity<Void> approveCredit(
            @PathVariable Long creditId,
            @RequestParam BigDecimal amountApproved,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        bankAnalystUseCase.approveCredit(creditId, amountApproved, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/credits/{creditId}/disburse")
    public ResponseEntity<Void> disbursCredit(
            @PathVariable Long creditId,
            @RequestParam String destinationAccount,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        bankAnalystUseCase.disbursCredit(creditId, destinationAccount, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/credits/{creditId}/reject")
    public ResponseEntity<Void> rejectCredit(
            @PathVariable Long creditId,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        bankAnalystUseCase.rejectCredit(creditId, Long.parseLong(document));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/binnacle")
    public ResponseEntity<List<Binnacle>> searchAllBinnacle() {
        return ResponseEntity.ok(bankAnalystUseCase.searchAllBinnacle());
    }

    @GetMapping("/binnacle/{productId}")
    public ResponseEntity<List<Binnacle>> searchBinnacleByProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(bankAnalystUseCase.searchBinnacleByProduct(productId));
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
}