package app.domain.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class BankAccount {
    private String accountNumber;
    private AccountType accountType;
    private Customer accountHolderID;
    private BigDecimal currentBalance;
    private Currency currency;
    private AccountStatement accountStatement;
    private Date openingDate;
}
