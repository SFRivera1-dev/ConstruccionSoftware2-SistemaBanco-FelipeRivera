package app.application.adapters.persistence.sql.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class PersonEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "document", nullable = false, unique = true, length = 20)
    private Long document;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "cellphone", nullable = false, length = 15)
    private String cellphone;

    @Column(name = "address", nullable = false, length = 200)
    private String address;
}