package app.application.adapters.api.controllers;

import app.application.adapters.api.request.AccountRequest;
import app.application.adapters.api.request.CreditRequest;
import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.CreditResponse;
import app.application.usecases.ProductAdvisorUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.Customer;
import app.domain.models.CustomerPerson;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-advisor")
public class ProductAdvisorController {

    private final ProductAdvisorUseCase productAdvisorUseCase;

    public ProductAdvisorController(ProductAdvisorUseCase productAdvisorUseCase) {
        this.productAdvisorUseCase = productAdvisorUseCase;
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(@PathVariable String accountNumber) {
        BankAccount account = productAdvisorUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @GetMapping("/clients/{document}")
    public ResponseEntity<?> searchClient(@PathVariable Long document) {
        return ResponseEntity.ok(productAdvisorUseCase.searchClient(document));
    }

    @PostMapping("/accounts")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        BankAccount account = toAccount(request);
        productAdvisorUseCase.createAccount(account, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toAccountResponse(account));
    }

    @PostMapping("/credits")
    public ResponseEntity<CreditResponse> createCredit(
            @Valid @RequestBody CreditRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Credit credit = toCredit(request);
        productAdvisorUseCase.createCredit(credit, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toCreditResponse(credit));
    }

    @GetMapping("/credits/{creditId}/status")
    public ResponseEntity<CreditResponse> searchCreditStatus(@PathVariable Long creditId) {
        Credit credit = productAdvisorUseCase.searchCreditStatus(creditId);
        return ResponseEntity.ok(toCreditResponse(credit));
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

    private static Credit toCredit(CreditRequest request) {
        Credit credit = new Credit();
        credit.setCreditType(request.getCreditType());
        credit.setAmountRequested(request.getAmountRequested());
        credit.setInterestRate(request.getInterestRate());
        credit.setTermMonths(request.getTermMonths());
        CustomerPerson customer = new CustomerPerson();
        customer.setDocument(request.getCustomerDocument());
        credit.setCustomerRequestId(customer);
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
}