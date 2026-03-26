package app.domain.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class credit {
    private Long idCredit;
    private creditType creditType;
    private customer customerRequestId;
    private BigDecimal amountRequested, amountApproved, interestRate;
    private Integer termMonths;
    private creditStatus creditStatus;
    private Date approvalDate, disbursementDate;
    private String destinationAccount;
}
