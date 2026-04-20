package app.application.adapters.api.response;

import app.domain.models.CreditStatus;
import app.domain.models.CreditType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class CreditResponse {
    private Long idCredit;
    private CreditType creditType;
    private BigDecimal amountRequested;
    private BigDecimal amountApproved;
    private BigDecimal interestRate;
    private Integer termMonths;
    private CreditStatus creditStatus;
    private Date approvalDate;
    private Date disbursementDate;
    private String destinationAccount;
    private Long customerDocument;
    private String customerName;
}