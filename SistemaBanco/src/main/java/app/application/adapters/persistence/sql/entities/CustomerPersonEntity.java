package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import app.domain.models.CustomerRole;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers_person")
public class CustomerPersonEntity extends PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "birthdate", nullable = false)
    private Date birthdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_role", nullable = false)
    private CustomerRole customerRole;
}