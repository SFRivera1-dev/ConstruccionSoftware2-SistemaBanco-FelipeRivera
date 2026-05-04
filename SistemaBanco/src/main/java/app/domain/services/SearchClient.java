package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Customer;
import app.domain.ports.ClientPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import app.domain.models.User;
import app.domain.ports.UserPort;

@Service
public class SearchClient {

    private final UserPort userPort;
    private final ClientPort clientPort;

    @Autowired
    public SearchClient(ClientPort clientPort, UserPort userPort) {
        this.clientPort = clientPort;
        this.userPort = userPort;
    }

    public Customer findByDocument(Long document) throws BusinessException {
        Customer customer = clientPort.findByDocument(document);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }
        return customer;
    }

    public User findUserByDocument(Long document) throws BusinessException {
        User user = userPort.findByDocument(document);
        if (user == null) {
            throw new BusinessException("No existe un usuario con ese documento");
        }
        return user;
    }

    public Customer findById(Long id) throws BusinessException {
        Customer customer = clientPort.findById(id);
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese ID");
        }
        return customer;
    }
}
