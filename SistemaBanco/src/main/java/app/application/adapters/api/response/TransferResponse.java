package app.application.adapters.api.response;

import app.domain.models.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class TransferResponse {
    private Long idTransfer;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal mount;
    private Date creationDate;
    private Date approvalDate;
    private TransferStatus transferStatus;
    private Long creatorUserId;
    private Long approvedUserId;
}