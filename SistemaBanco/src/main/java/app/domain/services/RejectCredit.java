package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;
import app.domain.models.Details;
import app.domain.models.Role;
import app.domain.ports.BinnaclePort;
import app.domain.ports.CreditPort;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class RejectCredit {

    private final CreditPort creditPort;
    private final BinnaclePort binnaclePort;

    public RejectCredit(CreditPort creditPort, BinnaclePort binnaclePort) {
        this.creditPort = creditPort;
        this.binnaclePort = binnaclePort;
    }

    public void rejectCredit(Long creditId, Long analystUserId) throws BusinessException {
        Credit credit = creditPort.findById(creditId);
        if (credit == null) {
            throw new BusinessException("No existe un crédito con ese ID");
        }

        if (credit.getCreditStatus() != CreditStatus.IN_STUDY) {
            throw new BusinessException("Solo se puede rechazar un crédito que esté en estado 'En estudio'");
        }

        creditPort.updateStatus(creditId, CreditStatus.REJECT);

        Details details = new Details();
        details.setPreviousStatus(CreditStatus.IN_STUDY.name());
        details.setNewStatus(CreditStatus.REJECT.name());

        Binnacle binnacle = new Binnacle();
        binnacle.setOperationType("Rechazo_Credito");
        binnacle.setDatetimeOperation(new Date(System.currentTimeMillis()));
        binnacle.setIdUser(analystUserId);
        binnacle.setRoleUser(Role.BANK_INTERNAL_ANALYST);
        binnacle.setAffectedProductId(String.valueOf(creditId));
        binnacle.setDetails(details);
        binnaclePort.save(binnacle);
    }
}