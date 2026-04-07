package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Credit;
import app.domain.models.Customer;
import app.domain.ports.ClientPort;
import app.domain.ports.CreditPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCredit {

    private final CreditPort creditPort;
    private final ClientPort clientPort;

    @Autowired
    public SearchCredit(CreditPort creditPort, ClientPort clientPort) {
        this.creditPort = creditPort;
        this.clientPort = clientPort;
    }

    public Credit findById(Long creditId) throws BusinessException {
        Credit credit = creditPort.findById(creditId);
        if (credit == null) {
            throw new BusinessException("No existe un crédito con ese ID");
        }
        return credit;
    }

    public List<Credit> findByCustomerDocument(Long document) throws BusinessException {
        Customer customer = clientPort.findByDocument(document);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }
        return creditPort.findByCustomerDocument(document);
    }
}
