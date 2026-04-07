package app.domain.ports;

import app.domain.models.Customer;
public interface ClientPort {
    Customer findByDocument(Long document);
    Customer findById(Long id);

}
