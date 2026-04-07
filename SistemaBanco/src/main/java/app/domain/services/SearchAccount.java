package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.BankAccount;
import app.domain.ports.AccountPort;
import app.domain.ports.ClientPort;
import app.domain.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchAccount {

    private final AccountPort accountPort;
    private final ClientPort clientPort;

    @Autowired
    public SearchAccount(AccountPort accountPort, ClientPort clientPort) {
        this.accountPort = accountPort;
        this.clientPort = clientPort;
    }

    // Buscar una cuenta por número (cualquier rol que la necesite)
    public BankAccount findByAccountNumber(String accountNumber) throws BusinessException {
        BankAccount account = accountPort.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new BusinessException("No existe una cuenta con ese número");
        }
        return account;
    }

    // Buscar todas las cuentas de un cliente — el cliente solo puede ver las suyas
    public List<BankAccount> findByCustomerDocument(Long document) throws BusinessException {
        Customer customer = clientPort.findByDocument(document);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }
        return accountPort.findByCustomerDocument(document);
    }
}
