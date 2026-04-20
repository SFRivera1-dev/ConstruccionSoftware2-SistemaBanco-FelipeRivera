package app.application.adapters.api.response;

import app.domain.models.AccountStatement;
import app.domain.models.AccountType;
import app.domain.models.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal currentBalance;
    private Currency currency;
    private AccountStatement accountStatement;
    private Date openingDate;
    private String holderName;
    private Long holderDocument;
}