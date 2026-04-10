package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import app.domain.models.CustomerRole;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers_company")
public class CustomerCompanyEntity extends PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legal_representative", nullable = false)
    private String legalRepresentative;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_role", nullable = false)
    private CustomerRole customerRole;
}