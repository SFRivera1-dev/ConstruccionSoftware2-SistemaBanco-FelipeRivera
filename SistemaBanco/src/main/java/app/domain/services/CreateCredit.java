package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;
import app.domain.models.Customer;
import app.domain.models.Role;
import app.domain.ports.BinnaclePort;
import app.domain.ports.ClientPort;
import app.domain.ports.CreditPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateCredit {

    private final CreditPort creditPort;
    private final ClientPort clientPort;
    private final BinnaclePort binnaclePort;

    @Autowired
    public CreateCredit(CreditPort creditPort, ClientPort clientPort, BinnaclePort binnaclePort) {
        this.creditPort = creditPort;
        this.clientPort = clientPort;
        this.binnaclePort = binnaclePort;
    }

    public void createCredit(Credit credit, Long creatorUserId, Role creatorRole) throws BusinessException {
        // Regla: el cliente debe existir y estar activo
        Customer customer = clientPort.findByDocument(credit.getCustomerRequestId().getDocument());
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }

        // Regla: el monto solicitado debe ser mayor que cero
        if (credit.getAmountRequested() == null || credit.getAmountRequested().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto solicitado debe ser mayor que cero");
        }

        // Regla: el plazo en meses debe ser mayor que cero
        if (credit.getTermMonths() == null || credit.getTermMonths() <= 0) {
            throw new BusinessException("El plazo del crédito debe ser mayor que cero");
        }

        // Regla: el tipo de crédito es obligatorio
        if (credit.getCreditType() == null) {
            throw new BusinessException("El tipo de crédito es obligatorio");
        }

        // Regla: la tasa de interés debe ser mayor que cero
        if (credit.getInterestRate() == null || credit.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La tasa de interés debe ser mayor que cero");
        }

        // Estado inicial siempre es EN ESTUDIO
        credit.setCreditStatus(CreditStatus.IN_STUDY);
        credit.setCustomerRequestId(customer);

        creditPort.save(credit);

        // Registrar en bitácora
        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Solicitud_Credito");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId(String.valueOf(credit.getIdCredit()));
        binnaclePort.save(binnacle);
    }
}
