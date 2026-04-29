package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import app.domain.models.TransferStatus;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_account", nullable = false)
    private BankAccountEntity originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account", nullable = false)
    private BankAccountEntity destinationAccount;

    @Column(name = "mount", nullable = false)
    private BigDecimal mount;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Column(name = "approval_date")
    private Date approvalDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", nullable = false)
    private TransferStatus transferStatus;

    @Column(name = "creator_user_id", nullable = false)
    private Long creatorUserId;

    @Column(name = "approved_user_id")
    private Long approvedUserId;
}