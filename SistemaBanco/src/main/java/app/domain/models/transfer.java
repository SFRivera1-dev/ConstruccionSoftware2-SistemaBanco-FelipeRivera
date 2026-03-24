package app.domain.models;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class transfer {
    private Long idTransfer;
    private bankAccount originAccount;
    private bankAccount destinationAccount;
    private BigDecimal mount;
    private Date creationDate, approvalDate;
    private transferStatus transferStatus;
    private Integer creatorUserId, approvedUserId;
}
