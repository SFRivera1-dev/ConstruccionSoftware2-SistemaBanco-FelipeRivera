package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor


public class bank_account {
    private String account_number;
    private account_type account_type;
    private String account_holderID;
    private Float current_balance;
    private String currency;
    private account_statement account_statement;
    private Date opening_date;
}
