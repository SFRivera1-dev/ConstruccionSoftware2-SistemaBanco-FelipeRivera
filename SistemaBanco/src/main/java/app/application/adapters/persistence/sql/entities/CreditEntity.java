package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import app.domain.models.CreditStatus;
import app.domain.models.CreditType;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "credits")
public class CreditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_type", nullable = false)
    private CreditType creditType;

    @ManyToOne
    @JoinColumn(name = "customer_person_id")
    private CustomerPersonEntity customerPerson;

    @ManyToOne
    @JoinColumn(name = "customer_company_id")
    private CustomerCompanyEntity customerCompany;

    @Column(name = "amount_requested", nullable = false)
    private BigDecimal amountRequested;

    @Column(name = "amount_approved")
    private BigDecimal amountApproved;

    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status", nullable = false)
    private CreditStatus creditStatus;

    @Column(name = "approval_date")
    private Date approvalDate;

    @Column(name = "disbursement_date")
    private Date disbursementDate;

    @Column(name = "destination_account", length = 20)
    private String destinationAccount;
}