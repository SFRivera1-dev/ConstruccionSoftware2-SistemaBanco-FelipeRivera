package app.domain.models;

import java.sql.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor

public class credit {
    private Long id_credit;
    private String credit_type, customer_request_id;
    private Float amount_requested, amount_approved, interest_rate;
    private Integer term_months;
    private credit_status credit_status;
    private Date approval_date, disbursement_date;
    private String destination_account;

}
