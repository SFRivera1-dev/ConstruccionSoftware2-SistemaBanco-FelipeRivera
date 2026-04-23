package app.application.adapters.api.controllers;

import app.application.adapters.api.request.CreditRequest;
import app.application.adapters.api.request.TransferRequest;
import app.application.adapters.api.response.AccountResponse;
import app.application.adapters.api.response.CreditResponse;
import app.application.adapters.api.response.TransferResponse;
import app.application.usecases.CustomerPersonUseCase;
import app.domain.models.BankAccount;
import app.domain.models.Credit;
import app.domain.models.CustomerPerson;
import app.domain.models.Transfer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-person")
public class CustomerPersonController {

    private final CustomerPersonUseCase customerPersonUseCase;

    public CustomerPersonController(CustomerPersonUseCase customerPersonUseCase) {
        this.customerPersonUseCase = customerPersonUseCase;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> searchMyAccounts(Authentication authentication) {
        String document = (String) authentication.getDetails();
        List<AccountResponse> accounts = customerPersonUseCase
                .searchMyAccounts(Long.parseLong(document))
                .stream().map(CustomerPersonController::toAccountResponse).toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<AccountResponse> searchAccount(@PathVariable String accountNumber) {
        BankAccount account = customerPersonUseCase.searchAccount(accountNumber);
        return ResponseEntity.ok(toAccountResponse(account));
    }

    @PostMapping("/credits")
    public ResponseEntity<CreditResponse> createCredit(
            @Valid @RequestBody CreditRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Credit credit = toCredit(request);
        customerPersonUseCase.createCredit(credit, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toCreditResponse(credit));
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        String document = (String) authentication.getDetails();
        Transfer transfer = toTransfer(request);
        customerPersonUseCase.createTransfer(transfer, Long.parseLong(document));
        return ResponseEntity.status(HttpStatus.CREATED).body(toTransferResponse(transfer));
    }

    @GetMapping("/credits")
    public ResponseEntity<List<CreditResponse>> searchMyCredits(Authentication authentication) {
        String document = (String) authentication.getDetails();
        List<CreditResponse> credits = customerPersonUseCase
                .searchMyCredits(Long.parseLong(document))
                .stream().map(CustomerPersonController::toCreditResponse).toList();
        return ResponseEntity.ok(credits);
    }

    @GetMapping("/credits/{creditId}")
    public ResponseEntity<CreditResponse> searchCredit(@PathVariable Long creditId) {
        Credit credit = customerPersonUseCase.searchCredit(creditId);
        return ResponseEntity.ok(toCreditResponse(credit));
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<TransferResponse> searchTransfer(@PathVariable Long transferId) {
        Transfer transfer = customerPersonUseCase.searchTransfer(transferId);
        return ResponseEntity.ok(toTransferResponse(transfer));
    }

    @GetMapping("/transfers/account/{accountNumber}")
    public ResponseEntity<List<TransferResponse>> searchMyTransfers(
            @PathVariable String accountNumber) {
        List<TransferResponse> transfers = customerPersonUseCase
                .searchMyTransfers(accountNumber)
                .stream().map(CustomerPersonController::toTransferResponse).toList();
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