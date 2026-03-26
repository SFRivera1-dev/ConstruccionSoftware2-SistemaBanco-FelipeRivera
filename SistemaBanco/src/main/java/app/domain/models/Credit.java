package app.domain.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class Credit {
    private Long idCredit;
    private CreditType creditType;
    private Customer customerRequestId;
    private BigDecimal amountRequested, amountApproved, interestRate;
    private Integer termMonths;
    private CreditStatus creditStatus;
    private Date approvalDate, disbursementDate;
    private String destinationAccount;
}
