package app.application.adapters.api.controllers;

import app.application.adapters.api.request.AccountRequest;
import app.application.adapters.api.request.DepositWithdrawRequest;
import app.application.adapters.api.response.AccountResponse;
import app.application.usecases.ServiceAdvisorUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-advisor")
public class ServiceAdvisorController {

    private final ServiceAdvisorUseCase serviceAdvisorUseCase;

    public ServiceAdvisorController(ServiceAdvisorUseCase serviceAdvisorUseCase) {
        this.serviceAdvisorUseCase = serviceAdvisorUseCase;
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(@PathVariable String accountNumber) {
        BankAccount account = serviceAdvisorUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @GetMapping("/accounts/customer/{document}")
    public ResponseEntity<List<AccountResponse>> searchAccountsByCustomer(
            @PathVariable Long document) {
        List<AccountResponse> accounts = serviceAdvisorUseCase.searchAccountsByCustomer(document)
                .stream().map(ServiceAdvisorController::toAccountResponse).toList();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Long userId = Long.parseLong(document);
        BankAccount account = toAccount(request);
        serviceAdvisorUseCase.createAccount(account, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAccountResponse(account));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @Valid @RequestBody DepositWithdrawRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Long userId = Long.parseLong(document);
        serviceAdvisorUseCase.deposit(request.getAccountNumber(), request.getAmount(), userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @Valid @RequestBody DepositWithdrawRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Long userId = Long.parseLong(document);
        serviceAdvisorUseCase.withdraw(request.getAccountNumber(), request.getAmount(), userId);
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

    private static BankAccount toAccount(AccountRequest request) {
        BankAccount account = new BankAccount();
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        Customer holder = new Customer();
        holder.setDocument(Long.parseLong(request.getHolderDocument()));
        account.setAccountHolderID(holder);
        return account;
    }
}