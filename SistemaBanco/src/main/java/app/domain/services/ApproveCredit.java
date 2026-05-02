package app.domain.services;

import app.domain.Exceptions.BusinessException;
import app.domain.models.AccountStatement;
import app.domain.models.BankAccount;
import app.domain.models.Binnacle;
import app.domain.models.Credit;
import app.domain.models.CreditStatus;
import app.domain.models.Details;
import app.domain.models.Role;
import app.domain.ports.AccountPort;
import app.domain.ports.BinnaclePort;
import app.domain.ports.CreditPort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;

@Service
public class ApproveCredit {

    private final CreditPort creditPort;
    private final AccountPort accountPort;
    private final BinnaclePort binnaclePort;

    public ApproveCredit(CreditPort creditPort, AccountPort accountPort, BinnaclePort binnaclePort) {
        this.creditPort = creditPort;
        this.accountPort = accountPort;
        this.binnaclePort = binnaclePort;
    }

    public void execute(Long creditId, BigDecimal amountApproved, String destinationAccount,
                        Long analystUserId, boolean disburse) throws BusinessException {

        Credit credit = creditPort.findById(creditId);
        if (credit == null) {
            throw new BusinessException("No existe un crédito con ese ID");
        }

        if (!disburse) {
            if (credit.getCreditStatus() != CreditStatus.IN_STUDY) {
                throw new BusinessException("Solo se puede aprobar un crédito en estado 'En estudio'");
            }
            if (amountApproved == null || amountApproved.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El monto aprobado debe ser mayor que cero");
            }

            credit.setIdCredit(creditId);
            credit.setAmountApproved(amountApproved);
            credit.setCreditStatus(CreditStatus.APPROVED);
            credit.setApprovalDate(new Date(System.currentTimeMillis()));
            creditPort.update(credit);

            Details details = new Details();
            details.setAmountApproved(amountApproved);
            details.setInterestRate(credit.getInterestRate());
            details.setPreviousStatus(CreditStatus.IN_STUDY.name());
            details.setNewStatus(CreditStatus.APPROVED.name());

            Binnacle binnacle = new Binnacle();
            binnacle.setOperationType("Aprobacion_Credito");
            binnacle.setDatetimeOperation(new java.util.Date());
            binnacle.setIdUser(analystUserId);
            binnacle.setRoleUser(Role.BANK_INTERNAL_ANALYST);
            binnacle.setAffectedProductId(String.valueOf(creditId));
            binnacle.setDetails(details);
            binnaclePort.save(binnacle);

        } else {
            if (credit.getCreditStatus() != CreditStatus.APPROVED) {
                throw new BusinessException("El desembolso solo es posible desde el estado 'Aprobado'");
            }
            if (destinationAccount == null || destinationAccount.isBlank()) {
                throw new BusinessException("Se debe definir la cuenta destino del desembolso");
            }
            BankAccount destAccount = accountPort.findByAccountNumber(destinationAccount);
            if (destAccount == null) {
                throw new BusinessException("La cuenta destino del desembolso no existe");
            }
            if (destAccount.getAccountStatement() != AccountStatement.ACTIVE) {
                throw new BusinessException("La cuenta destino del desembolso no está activa");
            }
            if (credit.getAmountApproved() == null || credit.getAmountApproved().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El monto aprobado debe ser mayor que cero para desembolsar");
            }

            BigDecimal balanceBefore = destAccount.getCurrentBalance();
            destAccount.setCurrentBalance(destAccount.getCurrentBalance().add(credit.getAmountApproved()));
            accountPort.save(destAccount);

            credit.setIdCredit(creditId);
            credit.setCreditStatus(CreditStatus.DISBURSED);
            credit.setDisbursementDate(new Date(System.currentTimeMillis()));
            credit.setDestinationAccount(destinationAccount);
            creditPort.update(credit);

            Details details = new Details();
            details.setAmountApproved(credit.getAmountApproved());
            details.setPreviousStatus(CreditStatus.APPROVED.name());
            details.setNewStatus(CreditStatus.DISBURSED.name());
            details.setAccountNumber(destinationAccount);
            details.setBalanceBeforeDestination(balanceBefore);
            details.setBalanceAfterDestination(destAccount.getCurrentBalance());

            Binnacle binnacle = new Binnacle();
            binnacle.setOperationType("Desembolso_Credito");
            binnacle.setDatetimeOperation(new java.util.Date());
            binnacle.setIdUser(analystUserId);
            binnacle.setRoleUser(Role.BANK_INTERNAL_ANALYST);
            binnacle.setAffectedProductId(String.valueOf(creditId));
            binnacle.setDetails(details);
            binnaclePort.save(binnacle);
        }
    }
}