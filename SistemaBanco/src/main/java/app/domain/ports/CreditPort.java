package app.domain.ports;

import java.util.List;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;

public interface CreditPort {
    void save(Credit credit);
    Credit findById(Long id);
    List<Credit> findByCustomerDocument(Long document);
    void updateStatus(Long creditId, CreditStatus newStatus);
    void update(Credit credit);

}
