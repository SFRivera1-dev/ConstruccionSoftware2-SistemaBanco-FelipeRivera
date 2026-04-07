package app.domain.ports;

import java.util.List;

import app.domain.models.Transfer;

public interface BatchPaymentPort {
    void saveAll(List<Transfer> trasnfers);
    List<Transfer> findByCompanyDocument(Long companyDocument);

}
