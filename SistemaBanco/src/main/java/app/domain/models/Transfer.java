package app.domain.models;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class Transfer {
    private Long idTransfer;
    private BankAccount originAccount;
    private BankAccount destinationAccount;
    private BigDecimal mount;
    private Date creationDate, approvalDate;
    private TransferStatus transferStatus;
    private Integer creatorUserId, approvedUserId;
}
