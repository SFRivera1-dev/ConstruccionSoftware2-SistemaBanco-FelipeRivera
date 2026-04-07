package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Credit;
import app.domain.ports.CreditPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchCreditStatus {

    private final CreditPort creditPort;

    @Autowired
    public SearchCreditStatus(CreditPort creditPort) {
        this.creditPort = creditPort;
    }

    // El asesor de productos puede consultar el estado de créditos en seguimiento
    // pero NO puede modificarlos
    public Credit findCreditStatus(Long creditId) throws BusinessException {
        Credit credit = creditPort.findById(creditId);
        if (credit == null) {
            throw new BusinessException("No existe un crédito con ese ID");
        }
        // Solo retorna el estado, no modifica nada
        return credit;
    }
}
