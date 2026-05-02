package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;
import app.domain.models.Customer;
import app.domain.models.Details;
import app.domain.models.Role;
import app.domain.ports.BinnaclePort;
import app.domain.ports.ClientPort;
import app.domain.ports.CreditPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class CreateCredit {

    private final CreditPort creditPort;
    private final ClientPort clientPort;
    private final BinnaclePort binnaclePort;

    public CreateCredit(CreditPort creditPort, ClientPort clientPort, BinnaclePort binnaclePort) {
        this.creditPort = creditPort;
        this.clientPort = clientPort;
        this.binnaclePort = binnaclePort;
    }

    public void createCredit(Credit credit, Long creatorUserId, Role creatorRole) throws BusinessException {
        Customer customer = clientPort.findByDocument(credit.getCustomerRequestId().getDocument());
        if (customer == null) {
            throw new BusinessException("No existe un cliente con ese documento");
        }

        if (credit.getAmountRequested() == null || credit.getAmountRequested().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto solicitado debe ser mayor que cero");
        }

        if (credit.getTermMonths() == null || credit.getTermMonths() <= 0) {
            throw new BusinessException("El plazo del crédito debe ser mayor que cero");
        }

        if (credit.getCreditType() == null) {
            throw new BusinessException("El tipo de crédito es obligatorio");
        }

        if (credit.getInterestRate() == null || credit.getInterestRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La tasa de interés debe ser mayor que cero");
        }

        credit.setCreditStatus(CreditStatus.IN_STUDY);
        credit.setCustomerRequestId(customer);

        creditPort.save(credit);

        Details details = new Details();
        details.setAmountApproved(credit.getAmountRequested());
        details.setInterestRate(credit.getInterestRate());
        details.setNewStatus(CreditStatus.IN_STUDY.name());

        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Solicitud_Credito");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(creatorUserId);
        binnacle.setRoleUser(creatorRole);
        binnacle.setAffectedProductId(String.valueOf(credit.getIdCredit()));
        binnacle.setDetails(details);
        binnaclePort.save(binnacle);
    }
}