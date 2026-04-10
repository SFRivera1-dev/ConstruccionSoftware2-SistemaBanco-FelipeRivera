package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import app.domain.models.AccountStatement;
import app.domain.models.AccountType;
import app.domain.models.Currency;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bank_accounts")
public class BankAccountEntity {

    @Id
    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerPersonEntity customerPerson;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CustomerCompanyEntity customerCompany;

    @Column(name = "current_balance", nullable = false)
    private BigDecimal currentBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_statement", nullable = false)
    private AccountStatement accountStatement;

    @Column(name = "opening_date", nullable = false)
    private Date openingDate;
}