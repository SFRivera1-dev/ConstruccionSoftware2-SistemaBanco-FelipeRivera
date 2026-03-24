package app.domain.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class bankAccount {
    private String accountNumber;
    private accountType accountType;
    private customer accountHolderID;
    private BigDecimal currentBalance;
    private currency currency;
    private accountStatement accountStatement;
    private Date openingDate;
}
