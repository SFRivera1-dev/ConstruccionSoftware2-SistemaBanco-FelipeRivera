package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Customer;
import app.domain.ports.ClientPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchClient {

    private final ClientPort clientPort;

    @Autowired
    public SearchClient(ClientPort clientPort) {
        this.clientPort = clientPort;
    }

    public Customer findByDocument(Long document) throws BusinessException {
        Customer customer = clientPort.findByDocument(document);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }
        return customer;
    }

    public Customer findById(Long id) throws BusinessException {
        Customer customer = clientPort.findById(id);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese ID");
        }
        return customer;
    }
}
