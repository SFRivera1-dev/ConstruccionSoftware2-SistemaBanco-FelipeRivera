package app.domain.ports;

import java.util.List;
import app.domain.models.BankAccount;

public interface AccountPort {
    void save(BankAccount account);
    BankAccount findByAccountNumber(String accountNumber);
    List<BankAccount> findByCustomerDocument(Long document);
}
